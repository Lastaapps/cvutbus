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

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import cz.lastaapps.cvutbus.components.settings.SettingsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val notificationHideKey = intPreferencesKey("notification_hide")
private val defaultNotificationHide = 0.seconds // Never

val SettingsStore.notificationHide: Flow<Duration>
    get() = store.data.map { it[notificationHideKey]?.seconds ?: defaultNotificationHide }

suspend fun SettingsStore.setNotificationHide(delay: Duration) {
    store.edit {
        it[notificationHideKey] = delay.inWholeSeconds.toInt()
    }
    log.i { "Storing notification hide delay ${delay.inWholeSeconds}" }
}
