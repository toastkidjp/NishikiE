package jp.toastkid.nishikie.libs

import android.content.Context
import android.content.SharedPreferences

/**
 * @author toastkidjp
 */
class PreferenceApplier(context: Context) {

    private enum class Key {
        IMAGE_PATH
    }

    private val preferences: SharedPreferences =
            context.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)

    var image: String
        get() = preferences.getString(Key.IMAGE_PATH.name, "")
        set(path) = preferences.edit().putString(Key.IMAGE_PATH.name, path).apply()

}
