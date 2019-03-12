package jp.toastkid.nishikie.libs

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * @author toastkidjp
 */
object BitmapScaling {

    private const val MAXIMUM_BITMAP_SIZE = 5000900f

    fun resizeImage(img: Bitmap, toSize: Float = MAXIMUM_BITMAP_SIZE): Bitmap {
        val size = img.byteCount
        val scale = Math.sqrt((toSize / size.toFloat()).toDouble()).toFloat()

        val matrix = Matrix()
        matrix.postScale(scale, scale)

        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

}