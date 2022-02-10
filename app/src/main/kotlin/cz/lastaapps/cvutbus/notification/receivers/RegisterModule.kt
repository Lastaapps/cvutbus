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

package cz.lastaapps.cvutbus.notification.receivers

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import cz.lastaapps.cvutbus.BuildConfig
import cz.lastaapps.cvutbus.components.settings.SettingsStore
import cz.lastaapps.cvutbus.components.settings.modules.NotificationStartup
import cz.lastaapps.cvutbus.components.settings.modules.notificationStartTime
import cz.lastaapps.cvutbus.components.settings.modules.notificationStartup
import cz.lastaapps.cvutbus.toLocalTime
import cz.lastaapps.entity.utils.CET
import kotlinx.coroutines.flow.first
import kotlinx.datetime.toJavaZoneId
import org.lighthousegames.logging.logging
import java.time.Instant
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class RegisterModule @Inject constructor(
    private val context: Application,
    private val store: SettingsStore,
) {
    companion object {
        private val log = logging()
    }

    private val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    suspend fun update() {
        cancel()

        when (store.notificationStartup.first()) {
            NotificationStartup.Disabled -> {}
            NotificationStartup.AlarmBased -> {
                getNextAlarm()?.let { next ->
                    registerWorkerStart(next, RegisteredReceiver.TYPE_ALARM)
                }
            }
            NotificationStartup.TimeBased -> {
                val time = store.notificationStartTime.first().toLocalTime()
                registerWorkerStart(time, RegisteredReceiver.TYPE_TIME)
            }
        }
    }

    private fun cancel(pendingIntent: PendingIntent = createPendingIntent("")) {
        log.i { "Canceling alarm" }
        manager.cancel(pendingIntent)
    }

    private fun registerWorkerStart(
        time: LocalTime,
        type: String,
        pendingIntent: PendingIntent = createPendingIntent(type)
    ) {

        var now = ZonedDateTime.now(CET.toJavaZoneId())
        if (now.toLocalTime() > time) {
            now = now.plusDays(1)
        }
        val toExecute = ZonedDateTime.of(now.toLocalDate(), time, CET.toJavaZoneId())

        log.i { "Scheduling aram on ${toExecute.format(DateTimeFormatter.ISO_DATE_TIME)}, type: $type" }
        if (BuildConfig.DEBUG) {
            Toast.makeText(
                context,
                "Alarm registered on ${toExecute.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}",
                Toast.LENGTH_LONG,
            ).show()
            Toast.makeText(context, "Alarm type is $type", Toast.LENGTH_LONG).show()
        }

        manager.set(
            AlarmManager.RTC,
            toExecute.toEpochSecond() * 1000,
            pendingIntent
        )
    }

    private fun getNextAlarm(): LocalTime? {
        return manager.nextAlarmClock?.triggerTime?.let { nextAlarm ->
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(nextAlarm), CET.toJavaZoneId())
                .toLocalDateTime().toLocalTime()
        }.also {
            log.i { "Next alarm: $it" }
        }
    }

    private fun createPendingIntent(type: String): PendingIntent {
        val intent = Intent(context, RegisteredReceiver::class.java)
        intent.putExtra(RegisteredReceiver.EXTRA_TYPE, type)

        return PendingIntent.getBroadcast(
            context, 17_080, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_IMMUTABLE else 0
        )
    }
}
