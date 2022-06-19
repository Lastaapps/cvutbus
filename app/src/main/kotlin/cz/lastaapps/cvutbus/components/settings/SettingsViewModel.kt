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

package cz.lastaapps.cvutbus.components.settings

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.cvutbus.api.DatabaseStore
import cz.lastaapps.cvutbus.api.worker.UpdateManager
import cz.lastaapps.cvutbus.components.settings.modules.*
import cz.lastaapps.cvutbus.minuteTicker
import cz.lastaapps.cvutbus.notification.receivers.RegisterModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class SettingsViewModel constructor(
    private val app: Application,
    val store: SettingsStore,
    val databaseStore: DatabaseStore,
    val updateManager: UpdateManager,
    private val registerModule: RegisterModule,
) : ViewModel() {

    fun setAppTheme(theme: AppThemeMode) {
        viewModelScope.launch {
            store.setAppTheme(theme)
        }
    }

    fun setDynamicTheme(enabled: Boolean) {
        viewModelScope.launch {
            store.setDynamicTheme(enabled)
        }
    }

    fun setPreferredStopPair(preferred: PreferredStopPair) {
        viewModelScope.launch {
            store.setPreferredStopPair(preferred)
        }
    }

    fun setPreferredDirection(preferred: PreferredDirection) {
        viewModelScope.launch {
            store.setPreferredDirection(preferred)
        }
    }

    fun setTimeShowMode(mode: TimeShowMode) {
        viewModelScope.launch {
            store.setTimeShowMode(mode)
        }
    }

    fun setNotificationHide(delay: Duration) {
        viewModelScope.launch {
            store.setNotificationHide(delay)
        }
    }

    fun setNotificationStartMode(mode: NotificationStartup) {
        viewModelScope.launch {
            store.setNotificationStartup(mode)
        }
    }

    fun setNotificationStartTime(time: Duration) {
        viewModelScope.launch {
            store.setNotificationStartTime(time)
        }
    }

    fun setNotificationWorkDaysOnly(only: Boolean) {
        viewModelScope.launch {
            store.setNotificationWorkDaysOnly(only)
        }
    }

    fun setBatteryDismissed(dismissed: Boolean) {
        viewModelScope.launch { store.setBatteryDismissed(dismissed) }
    }


    private val alarmFlow = MutableStateFlow(getNextAlarmTime())

    init {
        viewModelScope.launch {
            minuteTicker {
                updateNextAlarm()
            }
        }
    }

    fun updateNextAlarm() {
        viewModelScope.launch {
            registerModule.update()
            alarmFlow.emit(getNextAlarmTime())
        }
    }

    fun getAlarms(): StateFlow<Duration?> = alarmFlow

    private fun getNextAlarmTime(): Duration? {
        val manager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val instant = Instant.ofEpochMilli(manager.nextAlarmClock?.triggerTime ?: return null)
        val dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
        return dateTime.hour.hours + dateTime.minute.minutes +
                //some alarm app schedule one ore alarm about 20 before the real one
                if (dateTime.second > 0) 1.minutes else Duration.ZERO
    }
}
