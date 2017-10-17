package jp.toastkid.nishikie

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import jp.toastkid.nishikie.appwidget.Provider
import jp.toastkid.nishikie.appwidget.RemoteViewsFactory
import jp.toastkid.nishikie.libs.ImageFileLoader
import jp.toastkid.nishikie.libs.PreferenceApplier
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

/**
 * Main activity.
 *
 * @author toastkidjp
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fab.setOnClickListener { startActivityForResult(makePickImage(), IMAGE_READ_REQUEST) }
    }

    override fun onResume() {
        super.onResume()
        if (PreferenceApplier(this).image.isEmpty()) {
            return
        }
        ImageFileLoader.loadBitmap(this, Uri.fromFile(File(PreferenceApplier(this).image)))
                .let { setCurrentImage(it) }
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
        val id = item.itemId

        if (id == R.id.action_exit) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {

        if (requestCode == IMAGE_READ_REQUEST
                && resultCode == Activity.RESULT_OK
                && data != null) {
            val parcelFileDescriptor = this.contentResolver.openFileDescriptor(data.data, "r")
            val fileDescriptor = parcelFileDescriptor.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()

            val output = File(filesDir, "image.png")
            PreferenceApplier(this).image = output.path
            image.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(output))
            sendBroadcast(Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
        }
        super.onActivityResult(requestCode, resultCode, data)
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
