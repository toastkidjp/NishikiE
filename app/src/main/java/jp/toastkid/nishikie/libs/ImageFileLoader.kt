package jp.toastkid.nishikie.libs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.FileNotFoundException

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
        val parcelFileDescriptor = try {
            context.contentResolver.openFileDescriptor(uri, "r")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor) ?: return null
        parcelFileDescriptor?.close()
        return BitmapScaling(context).resizeImage(image)
    }

}