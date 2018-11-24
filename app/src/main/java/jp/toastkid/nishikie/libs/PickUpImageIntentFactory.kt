package jp.toastkid.nishikie.libs

import android.content.Intent

/**
 * @author toastkidjp
 */
object PickUpImageIntentFactory {

    /**
     * Make pick image intent.
     * @return [Intent]
     */
    operator fun invoke(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        return intent
    }
}