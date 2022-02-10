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

package cz.lastaapps.cvutbus.api.worker

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cz.lastaapps.cvutbus.MainActivity
import cz.lastaapps.cvutbus.R
import org.lighthousegames.logging.logging

class UpdateNotifications(private val context: Context) {

    companion object {
        private const val channelId = "update_channel"
        private val log = logging()
    }

    fun createUpdatingNotification(): Notification {
        return setupBuilder(
            R.string.update_notification_updating_title,
            R.string.update_notification_updating_content,
        ).build()
    }

    fun createDoneNotification(): Notification {
        return setupBuilder(
            R.string.update_notification_done_title,
            R.string.update_notification_done_content,
        ).build()
    }

    fun createFailedNotification(): Notification {
        return setupBuilder(
            R.string.update_notification_failed_title,
            R.string.update_notification_failed_content,
        ).build()
    }

    fun createNoNewAvailableNotification(): Notification {
        return setupBuilder(
            R.string.update_notification_no_new_title,
            R.string.update_notification_no_new_content,
        ).build()
    }

    private fun setupBuilder(
        title: Int,
        description: Int,
    ): NotificationCompat.Builder {
        log.i { "Creating notification t: $title, d: $description" }

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPending = PendingIntent.getActivity(
            context, 39221, contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0
        )

        return with(NotificationCompat.Builder(context, channelId)) {
            setContentTitle(context.getString(title))
            setTicker(context.getString(title))
            setContentText(context.getString(description))
            setSmallIcon(R.drawable.notification_icon)
            setContentIntent(contentPending)
            setOngoing(false)
            setLocalOnly(true)
            setAutoCancel(false)
            setAllowSystemGeneratedContextualActions(false)
            setShowWhen(false)
            setSilent(true)
            setVibrate(null)
        }
    }

    private fun createNotificationChannel() {
        NotificationManagerCompat.from(context).createNotificationChannel(
            with(
                NotificationChannelCompat.Builder(
                    channelId, NotificationManagerCompat.IMPORTANCE_DEFAULT
                )
            ) {
                setName(context.getString(R.string.update_notification_channel_name))
                setDescription(context.getString(R.string.update_notification_channel_description))
                setShowBadge(false)
                setVibrationEnabled(false)
                build()
            })

    }
}