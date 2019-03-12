package jp.toastkid.nishikie.libs

import android.content.Context
import android.support.v7.app.AlertDialog
import jp.toastkid.nishikie.R
import okio.Okio
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * License files viewer.
 *
 * Initialize with context.
 * @param context For using makeNew assets and show dialog.
 *
 * @author toastkidjp
 */
class LicenseViewer(private val context: Context) {

    /**
     * Invoke viewer.
     */
    operator fun invoke() {
        try {
            val assets = context.assets
            val licenseFiles = assets.list(DIRECTORY_OF_LICENSES) ?: emptyArray()
            val licenseMap = LinkedHashMap<String, String>(licenseFiles.size)
            for (fileName in licenseFiles) {
                val stream = assets.open("$DIRECTORY_OF_LICENSES/$fileName")
                licenseMap[fileName.substring(0, fileName.lastIndexOf("."))] = readUtf8(stream)
                stream.close()
            }
            val items = licenseMap.keys.toTypedArray()
            AlertDialog.Builder(context).setTitle(R.string.title_licenses)
                    .setItems(items
                    ) { _, index ->
                        AlertDialog.Builder(context)
                                .setTitle(items[index])
                                .setMessage(licenseMap[items[index]])
                                .setCancelable(true)
                                .setPositiveButton(R.string.close) { dialog, _ -> dialog.dismiss() }
                                .show()
                    }
                    .setCancelable(true)
                    .setPositiveButton(R.string.close) { d, _ -> d.dismiss() }
                    .show()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * Read string from passed [InputStream].
     *
     * @param inputStream [InputStream]]
     */
    @Throws(IOException::class)
    private fun readUtf8(inputStream: InputStream): String
            = Okio.buffer(Okio.source(inputStream)).readUtf8()

    companion object {

        /**
         * Directory name.
         */
        private const val DIRECTORY_OF_LICENSES = "licenses"
    }
}
