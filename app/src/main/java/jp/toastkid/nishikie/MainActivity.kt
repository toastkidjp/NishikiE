package jp.toastkid.nishikie

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import jp.toastkid.nishikie.appwidget.Provider
import jp.toastkid.nishikie.appwidget.RemoteViewsFactory
import jp.toastkid.nishikie.libs.BitmapScaling
import jp.toastkid.nishikie.libs.ImageFileLoader
import jp.toastkid.nishikie.libs.LicenseViewer
import jp.toastkid.nishikie.libs.PreferenceApplier
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

/**
 * Main activity.
 *
 * @author toastkidjp
 */
class MainActivity : AppCompatActivity() {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fab.setOnClickListener { startActivityForResult(makePickImage(), IMAGE_READ_REQUEST) }
    }

    /**
     * Make pick image intent.
     * @return [Intent]
     */
    private fun makePickImage(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        return intent
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_license -> {
                LicenseViewer(this).invoke()
                return true
            }
            R.id.action_privacy_policy -> {
                CustomTabsIntent.Builder()
                        .setShowTitle(true)
                        .setStartAnimations(this, 0, 0)
                        .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addDefaultShareMenuItem()
                        .build()
                        .launchUrl(this, Uri.parse(getString(R.string.link_privacy_policy)))
                return true
            }
            R.id.action_exit -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
            val executor = Executors.newSingleThreadExecutor()
            executor.submit { loadImage(imageUri) }
            //executor.shutdown()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadImage(imageUri: Uri) {
        mainThreadHandler.post { progress.visibility = View.VISIBLE }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inScaled = true

        val image = ImageFileLoader.loadBitmap(this, imageUri)
                ?.let { BitmapScaling(this).resizeImage(it) }

        val output = File(filesDir, "image.png")
        PreferenceApplier(this).image = output.path
        image?.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(output))
        sendBroadcast(Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
        mainThreadHandler.post {
            setCurrentImage(image)
            progress.visibility = View.GONE
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

        fun makeIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }
    }
}
