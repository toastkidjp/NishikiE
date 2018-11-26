/*
 * Copyright (c) 2018 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.nishikie.appwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

/**
 * App-Widget refresher. This class only call UPDATE broadcast.
 *
 * @param context
 * @author toastkidjp
 */
class AppWidgetRefresher(private val context: Context) {

    /**
     * Invoke refreshing.
     */
    operator fun invoke() = LocalBroadcastManager.getInstance(context)
            .sendBroadcast(Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
}