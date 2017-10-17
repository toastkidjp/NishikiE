package jp.toastkid.nishikie.appwidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.RemoteViews
import jp.toastkid.nishikie.R
import jp.toastkid.nishikie.libs.PreferenceApplier
import java.io.File

/**
 * App Widget's RemoteViews factory.
 *
 * @author toastkidjp
 */
internal object RemoteViewsFactory {

    /**
     * Method name.
     */
    private val METHOD_NAME_SET_COLOR_FILTER = "setColorFilter"

    /**
     * Method name.
     */
    private val METHOD_NAME_SET_BACKGROUND_COLOR = "setBackgroundColor"

    /**
     * Layout ID.
     */
    private val APPWIDGET_LAYOUT_ID = R.layout.appwidget_sticky

    /**
     * Make RemoteViews.
     *
     * @param context
     *
     * @return RemoteViews
     */
    fun make(context: Context): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, APPWIDGET_LAYOUT_ID)
        val imagePath = PreferenceApplier(context).image
        println("imagePath=${imagePath}")
        if (imagePath.isNotEmpty()) {
            loadBitmap(context, Uri.fromFile(File(imagePath)))
                    .let { remoteViews.setImageViewBitmap(R.id.image, it) }
        }
        setTapActions(context, remoteViews)
        return remoteViews
    }

    /**
     * Load bitmap from file URI.
     *
     * @param context [Context]
     * @param uri [Uri]
     * @return Nullable [Bitmap]
     */
    private fun loadBitmap(context: Context, uri: Uri): Bitmap? {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor) ?: return null
        parcelFileDescriptor.close()
        return image
    }

    /**
     * Set pending intents.
     *
     * @param context
     * @param remoteViews
     */
    private fun setTapActions(context: Context, remoteViews: RemoteViews) {
        /*remoteViews.setOnClickPendingIntent(
                R.id.widget_search, PendingIntentFactory.makeSearchLauncher(context))
        remoteViews.setOnClickPendingIntent(
                R.id.widget_launcher, PendingIntentFactory.launcher(context))
        remoteViews.setOnClickPendingIntent(
                R.id.widget_barcode_reader, PendingIntentFactory.barcode(context))*/
    }

}