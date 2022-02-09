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

package cz.lastaapps.cvutbus.components.settings.modules

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import cz.lastaapps.cvutbus.components.settings.SettingsStore
import cz.lastaapps.cvutbus.notification.receivers.RegisterModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed class NotificationStartup(val id: Int) {
    object Disabled : NotificationStartup(0)
    object AlarmBased : NotificationStartup(1)
    object TimeBased : NotificationStartup(2)
}

private val notificationStartupKey = intPreferencesKey("notification_startup")
private val defaultNotificationStartup = NotificationStartup.AlarmBased

val SettingsStore.notificationStartup: Flow<NotificationStartup>
    get() = store.data.map {
        when (it[notificationStartupKey]) {
            NotificationStartup.Disabled.id -> NotificationStartup.Disabled
            NotificationStartup.AlarmBased.id -> NotificationStartup.AlarmBased
            NotificationStartup.TimeBased.id -> NotificationStartup.TimeBased
            else -> defaultNotificationStartup
        }
    }

suspend fun SettingsStore.setNotificationStartup(mode: NotificationStartup) {
    store.edit { it[notificationStartupKey] = mode.id }
    RegisterModule(app, this).update()
}


// stores time in seconds
private val notificationStartTimeKey = intPreferencesKey("notification_start_time")
private val defaultNotificationStartTime = (7 * 3600).seconds

val SettingsStore.notificationStartTime: Flow<Duration>
    get() = store.data.map { it[notificationStartTimeKey]?.seconds ?: defaultNotificationStartTime }

suspend fun SettingsStore.setNotificationStartTime(duration: Duration) {
    store.edit { it[notificationStartTimeKey] = duration.inWholeSeconds.toInt() }
    RegisterModule(app, this).update()
}


private val notificationWorkDaysOnlyKey = booleanPreferencesKey("notification_work_days_only")
private const val defaultNotificationWorkDaysOnly = true

val SettingsStore.notificationWorkDaysOnly: Flow<Boolean>
    get() = store.data.map { it[notificationWorkDaysOnlyKey] ?: defaultNotificationWorkDaysOnly }

suspend fun SettingsStore.setNotificationWorkDaysOnly(onlyWorkDays: Boolean) {
    store.edit { it[notificationWorkDaysOnlyKey] = onlyWorkDays }
}
