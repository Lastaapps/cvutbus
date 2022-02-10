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
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cz.lastaapps.cvutbus.R
import org.lighthousegames.logging.logging

class UpdateNotifications(private val context: Context) {

    companion object {
        private const val channelId = "update_channel"
        private val log = logging()
    }

    fun createUpdatingNotification(): Notification {
        return setupBuilder(
            "Updating offline data",
            "So you wouldn't miss a thing"
        ).build()
    }

    fun createDoneNotification(): Notification {
        return setupBuilder(
            "Data updated",
            "Get ready for more adventures",
        ).build()
    }

    fun createFailedNotification(): Notification {
        return setupBuilder(
            "Failed to update data",
            "Connection may be inaccurate in few days",
        ).build()
    }

    fun createNoNewAvailableNotification(): Notification {
        return setupBuilder(
            "No new data available",
            "Try updating few days later",
        ).build()
    }

    private fun setupBuilder(
        title: String,
        description: String,
    ): NotificationCompat.Builder {
        log.i { "Creating notification t: $title, d: $description" }

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        return with(NotificationCompat.Builder(context, channelId)) {
            setContentTitle(title)
            setContentText(description)
            setTicker(title)
            setSmallIcon(R.drawable.notification_icon)
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
                setName("TODO")
                setDescription("TODO")
                setShowBadge(false)
                setVibrationEnabled(false)
                build()
            })

    }
}