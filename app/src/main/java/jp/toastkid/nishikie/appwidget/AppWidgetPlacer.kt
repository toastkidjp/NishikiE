package jp.toastkid.nishikie.appwidget

import android.annotation.TargetApi
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle

/**
 * @author toastkidjp
 */
class AppWidgetPlacer(private val context: Context) {

    private val appWidgetManager = AppWidgetManager.getInstance(context)

    private val targetAppWidgetProviderName = Provider::class.java.canonicalName

    @TargetApi(Build.VERSION_CODES.O)
    operator fun invoke() {
        appWidgetManager.requestPinAppWidget(
                ComponentName.createRelative(context, targetAppWidgetProviderName),
                Bundle(),
                null
        )
    }

    fun isTargetOs() =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    && appWidgetManager.isRequestPinAppWidgetSupported
}