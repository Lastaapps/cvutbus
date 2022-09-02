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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import cz.lastaapps.cvutbus.BuildConfig
import cz.lastaapps.cvutbus.components.settings.SettingsStore
import cz.lastaapps.cvutbus.components.settings.modules.notificationWorkDaysOnly
import cz.lastaapps.cvutbus.notification.WorkerUtils
import cz.lastaapps.cvutbus.ui.SafeToast
import cz.lastaapps.entity.utils.CET
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.datetime.toJavaZoneId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class RegisteredReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        const val EXTRA_TYPE = "type"
        const val TYPE_ALARM = "alarm"
        const val TYPE_TIME = "time"

        private val log = logging()
    }

    override fun onReceive(context: Context, intent: Intent) {
        log.i { "Received" }
        val registerModule: RegisterModule by inject()
        val store: SettingsStore by inject()

        runBlocking {
            if (shouldStart(intent, store)) {
                log.i { "Starting worker" }
                if (BuildConfig.DEBUG) {
                    SafeToast.makeTextAndShow(context, "Notification started", Toast.LENGTH_LONG)
                }
                WorkerUtils(context).start()
            }

            log.i { "Updating" }
            registerModule.update()

            CoroutineScope(Dispatchers.Default).launch {
                delay(5000) // next alarm may be still null
                registerModule.update()
            }
        }
    }

    private suspend fun shouldStart(intent: Intent, store: SettingsStore): Boolean {
        if (!canRunWeekEnd(store)) return false

        return when (intent.getStringExtra(EXTRA_TYPE)!!) {
            TYPE_ALARM -> {
                val now = LocalTime.now(CET.toJavaZoneId())
                now.hour < 12
            }
            TYPE_TIME -> true
            else -> error("Unknown type")
        }
    }

    private suspend fun canRunWeekEnd(store: SettingsStore): Boolean {
        val today = LocalDate.now(CET.toJavaZoneId())
        val isWeekend = today.dayOfWeek in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        if (isWeekend) {
            return !store.notificationWorkDaysOnly.first()
        }
        return true
    }
}