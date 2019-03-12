package jp.toastkid.nishikie.libs

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * @author toastkidjp
 */
class BitmapScaling {

    fun resizeImage(img: Bitmap, toSize: Float = MAXIMUM_BITMAP_SIZE): Bitmap {
        val size = img.byteCount
        val scale = Math.sqrt((toSize / size.toFloat()).toDouble()).toFloat()

        val matrix = Matrix()
        matrix.postScale(scale, scale)

        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

    companion object {
        private const val MAXIMUM_BITMAP_SIZE = 5000900f
    }
}