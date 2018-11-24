package jp.toastkid.nishikie

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import jp.toastkid.nishikie.appwidget.AppWidgetPlacer
import jp.toastkid.nishikie.appwidget.Provider
import jp.toastkid.nishikie.appwidget.RemoteViewsFactory
import jp.toastkid.nishikie.libs.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Main activity.
 *
 * @author toastkidjp
 */
class MainActivity : AppCompatActivity() {

    /**
     * App widget place invoker.
     */
    private lateinit var appWidgetPlacer: AppWidgetPlacer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        appWidgetPlacer = AppWidgetPlacer(this)

        fab.setOnClickListener { startActivityForResult(PickUpImageIntentFactory(), IMAGE_READ_REQUEST) }

        ImageFileLoader.loadBitmap(this, Uri.fromFile(File(PreferenceApplier(this).image)))
                ?.let { setCurrentImage(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        if (appWidgetPlacer.isTargetOs()) {
            menuInflater.inflate(R.menu.menu_place_app_widget, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_place_app_widget -> {
            appWidgetPlacer()
            true
        }
        R.id.action_license -> {
            LicenseViewer(this).invoke()
            true
        }
        R.id.action_privacy_policy -> {
            CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .setStartAnimations(this, 0, 0)
                    .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .addDefaultShareMenuItem()
                    .build()
                    .launchUrl(this, Uri.parse(getString(R.string.link_privacy_policy)))
            true
        }
        R.id.action_exit -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {

        val imageUri = data?.data
        if (requestCode == IMAGE_READ_REQUEST
                && resultCode == Activity.RESULT_OK
                && imageUri != null
        ) {
            GlobalScope.launch {
                loadImage(imageUri)
            }.start()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Load bitmap from image uri.
     *
     * @param imageUri [Uri]
     */
    private fun loadImage(imageUri: Uri) {
        GlobalScope.launch(Dispatchers.Main) { progress.visibility = View.VISIBLE }

        val image = ImageFileLoader.loadBitmap(this, imageUri)
                ?.let { BitmapScaling(this).resizeImage(it) }

        val output = File(filesDir, "image.png")
        PreferenceApplier(this).image = output.path
        image?.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(output))

        sendBroadcast(Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE))

        GlobalScope.launch(Dispatchers.Main) {
            setCurrentImage(image)
            progress.visibility = View.GONE
            if (appWidgetPlacer.isTargetOs()) {
                val snackbar = Snackbar.make(
                        main_content,
                        R.string.message_confirm_place_app_widget,
                        Snackbar.LENGTH_LONG
                )
                snackbar.setAction(R.string.action_place_app_widget) { appWidgetPlacer() }
                snackbar.show()
            }
        }
    }

    /**
     * Set current image to preview-area and app-widget.
     * @param bitmap Nullable [Bitmap]
     */
    private fun setCurrentImage(bitmap: Bitmap?) {
        current_image.setImageBitmap(bitmap)
        Provider.updateWidget(this, RemoteViewsFactory.make(this))
    }

    companion object {

        /**
         * Internal request code.
         */
        private const val IMAGE_READ_REQUEST: Int = 1

        /**
         * Make this activity's intent.
         *
         * @param context [Context]
         */
        fun makeIntent(context: Context) =
                Intent(context, MainActivity::class.java)
                        .also { it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
    }
}
