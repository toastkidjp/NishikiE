package jp.toastkid.nishikie.libs

import android.content.Intent

/**
 * @author toastkidjp
 */
class PickUpImageIntentFactory {

    /**
     * Make pick image intent.
     * @return [Intent]
     */
    operator fun invoke() = Intent(Intent.ACTION_GET_CONTENT)
            .also {
                it.addCategory(Intent.CATEGORY_OPENABLE)
                it.type = "image/*"
            }
}