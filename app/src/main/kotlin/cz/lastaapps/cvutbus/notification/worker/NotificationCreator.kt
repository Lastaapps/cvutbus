/*
 * Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 * This file is part of ČVUT Bus.
 *
 * ČVUT Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ČVUT Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ČVUT Bus.  If not, see <https://www.gnu.org/licenses/>.
 */

package cz.lastaapps.cvutbus.notification.worker

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import cz.lastaapps.cvutbus.MainActivity
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.localizedFormat
import cz.lastaapps.cvutbus.notification.receivers.ChangeDirectionReceiver
import cz.lastaapps.entity.utils.CET
import cz.lastaapps.repo.DepartureInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.toInstant
import org.lighthousegames.logging.logging
import java.util.*

class NotificationCreator(private val context: Context, private val workerId: UUID) {

    companion object {
        private const val channelId = "notification_worker"
        private val log = logging()
    }

    fun createPlaceholderNotification(): Notification {
        return setupBuilder(
            null,
            R.string.notification_create_placeholder_title,
            R.string.notification_create_placeholder_content,
        ).build()
    }

    fun createTimeNotification(data: List<DepartureInfo>): Notification {
        return if (data.isEmpty()) {
            setupBuilder(
                null,
                R.string.notification_create_time_no_data_title,
                R.string.notification_create_time_no_data_content,
            ).build()
        } else {
            setupBuilder(
                data.first().connection.to.name,
                createTimeTitle(data.first()),
                createTimeDescription(data.drop(1)),
            ).build()
        }
    }

    private fun createTimeTitle(info: DepartureInfo): String {
        val duration = info.dateTime.toInstant(CET) - Clock.System.now()

        val time = if (duration.inWholeHours == 0L) {
            if (duration.inWholeMinutes == 0L)
                context.getString(R.string.notification_create_time_data_now)
            else
                "${duration.inWholeMinutes} ${context.getString(R.string.notification_create_time_data_minutes_abbrev)}"
        } else {
            "%d:%02d".format(duration.inWholeHours, duration.inWholeMinutes % 60)
        }
        return "${info.routeShortName} ${
            info.dateTime.localizedFormat(context)
        } $time"
    }

    private fun createTimeDescription(data: List<DepartureInfo>): String {
        return data.joinToString(separator = ", ") {
            it.dateTime.localizedFormat(context)
        }
    }

    fun createAutoHideNotification(): Notification {
        return setupBuilder(
            null,
            R.string.notification_create_hide_title,
            R.string.notification_create_hide_content,
        ).build()
    }

    private fun setupBuilder(
        @StringRes header: Int?,
        @StringRes title: Int,
        @StringRes description: Int,
    ): NotificationCompat.Builder = setupBuilder(
        header?.let { context.getString(header) },
        context.getString(title),
        context.getString(description),
    )

    private fun setupBuilder(
        header: String?,
        title: String,
        description: String,
    ): NotificationCompat.Builder {
        log.i {
            "Creating notification h: $header, t: $title, d: $description"
        }

        val cancelIntent = WorkManager.getInstance(context)
            .createCancelPendingIntent(workerId)

        val reverse = PendingIntent.getBroadcast(
            context,
            23569,
            Intent(context, ChangeDirectionReceiver::class.java),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPending = PendingIntent.getActivity(
            context, 39221, contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0
        )

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        return with(NotificationCompat.Builder(context, channelId)) {
            setContentTitle(title)
            setTicker(title)
            header?.let {
                setSubText(header)
            }
            setContentText(description)
            //setStyle(NotificationCompat.BigTextStyle())
            setSmallIcon(R.drawable.notification_icon)
            setContentIntent(contentPending)
            addAction(
                android.R.drawable.ic_menu_rotate,
                context.getString(R.string.notification_button_direction),
                reverse,
            )
            addAction(
                android.R.drawable.ic_delete,
                context.getString(R.string.notification_button_close),
                cancelIntent,
            )
            setOngoing(true)
            setLocalOnly(true)
            setAutoCancel(false)
            setAllowSystemGeneratedContextualActions(false)
            setShowWhen(false)
            setSilent(true)
            setVibrate(null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        NotificationManagerCompat.from(context).createNotificationChannel(
            with(
                NotificationChannelCompat.Builder(
                    channelId, NotificationManagerCompat.IMPORTANCE_DEFAULT
                )
            ) {
                setName(context.getString(R.string.notification_channel_name))
                setDescription(context.getString(R.string.notification_channel_description))
                setShowBadge(false)
                setVibrationEnabled(false)
                build()
            })
    }
}