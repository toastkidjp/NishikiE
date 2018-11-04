package jp.toastkid.nishikie.libs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

/**
 * Load image bitmap from file.
 *
 * @author toastkidjp
 */
object ImageFileLoader {

    /**
     * Load bitmap from file URI.
     *
     * @param context [Context]
     * @param uri [Uri]
     * @return Nullable [Bitmap]
     */
    fun loadBitmap(context: Context, uri: Uri): Bitmap? {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor) ?: return null
        parcelFileDescriptor?.close()
        return image
    }

}