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
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.localizedFormat
import cz.lastaapps.cvutbus.notification.receivers.ChangeDirectionReceiver
import cz.lastaapps.entity.utils.CET
import cz.lastaapps.repo.DepartureInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.toInstant
import org.lighthousegames.logging.logging
import java.util.*

class NotificationCreator(private val appContext: Context, private val workerId: UUID) {

    companion object {
        private const val channelId = "notification_worker"
        private val log = logging()
    }

    fun createPlaceholderNotification(): Notification {
        return setupBuilder(null, "Wait a minute", "Let me think").build()
    }

    fun createTimeNotification(data: List<DepartureInfo>): Notification {
        return if (data.isEmpty()) {
            setupBuilder(
                null,
                "No data available",
                "Try to update data or contact the app developer",
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
                "Now" else "${duration.inWholeMinutes} min"
        } else {
            "%d:%02d".format(duration.inWholeHours, duration.inWholeMinutes % 60)
        }
        return "${info.routeShortName} ${
            info.dateTime.localizedFormat(appContext)
        } $time"
    }

    private fun createTimeDescription(data: List<DepartureInfo>): String {
        return data.joinToString(separator = ", ") {
            it.dateTime.localizedFormat(appContext)
        }
    }

    fun createAutoHideNotification(): Notification {
        return setupBuilder(
            null,
            "Auto hide limit reached",
            "Notification is going to be dismissed",
        ).build()
    }

    private fun setupBuilder(
        header: String?,
        title: String,
        description: String,
    ): NotificationCompat.Builder {
        log.i { "Creating notification h: $header, t: $title, d: $description" }

        val cancelIntent = WorkManager.getInstance(appContext)
            .createCancelPendingIntent(workerId)

        val reverse = PendingIntent.getBroadcast(
            appContext,
            23569,
            Intent(appContext, ChangeDirectionReceiver::class.java),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        return with(NotificationCompat.Builder(appContext, channelId)) {
            setContentTitle(title)
            header?.let {
                setSubText(header)
            }
            setContentText(description)
            setTicker(title)
            //setStyle(NotificationCompat.BigTextStyle())
            setSmallIcon(R.drawable.notification_icon)
            addAction(android.R.drawable.ic_menu_rotate, "Switch direction", reverse)
            addAction(android.R.drawable.ic_delete, "Close", cancelIntent)
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
        NotificationManagerCompat.from(appContext).createNotificationChannel(
            with(
                NotificationChannelCompat.Builder(
                    channelId, NotificationManagerCompat.IMPORTANCE_DEFAULT
                )
            ) {
                setName("TODO")
                setDescription("TODO")
                setShowBadge(false)
                setVibrationEnabled(false)
                build()
            })
    }
}