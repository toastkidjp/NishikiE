package jp.toastkid.nishikie.appwidget

import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.widget.RemoteViews
import jp.toastkid.nishikie.MainActivity
import jp.toastkid.nishikie.R
import jp.toastkid.nishikie.libs.ImageFileLoader
import jp.toastkid.nishikie.libs.PreferenceApplier
import java.io.File

/**
 * App Widget's RemoteViews factory.
 *
 * @author toastkidjp
 */
internal object RemoteViewsFactory {

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
        if (imagePath.isNotEmpty()) {
            ImageFileLoader.loadBitmap(context, Uri.fromFile(File(imagePath)))
                    .let { remoteViews.setImageViewBitmap(R.id.image, it) }
        }
        setTapActions(context, remoteViews)
        return remoteViews
    }

    /**
     * Set pending intents.
     *
     * @param context
     * @param remoteViews
     */
    private fun setTapActions(context: Context, remoteViews: RemoteViews) {
        remoteViews.setOnClickPendingIntent(
                R.id.image,
                PendingIntent.getActivity(
                        context,
                        1,
                        MainActivity.makeIntent(context),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        )
    }

}