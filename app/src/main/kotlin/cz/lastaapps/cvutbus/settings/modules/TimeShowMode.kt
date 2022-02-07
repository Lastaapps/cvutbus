/*
 * Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 * This file is part of ČVUT Bus.
 *
 * Menza is free software: you can redistribute it and/or modify
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

package cz.lastaapps.cvutbus.settings.modules

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import cz.lastaapps.cvutbus.settings.SettingsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class TimeShowMode(val id: Int) {
    object Remember : TimeShowMode(0)
    object Countdown : TimeShowMode(1)
    object Time : TimeShowMode(2)
}

private val timeShowModeKey = intPreferencesKey("time_show_mode")
private val defaultShowCounter = TimeShowMode.Remember
private val latestWasCounterKey = booleanPreferencesKey("latest_was_counter")
private const val defaultWasCounter = true

val SettingsStore.timeShowMode: Flow<TimeShowMode>
    get() = store.data.map {
        when (it[timeShowModeKey]) {
            TimeShowMode.Remember.id -> TimeShowMode.Remember
            TimeShowMode.Countdown.id -> TimeShowMode.Countdown
            TimeShowMode.Time.id -> TimeShowMode.Time
            else -> defaultShowCounter
        }
    }

suspend fun SettingsStore.setTimeShowMode(mode: TimeShowMode) {
    store.edit { it[timeShowModeKey] = mode.id }
}

val SettingsStore.latestWasCounter: Flow<Boolean>
    get() = store.data.map { it[latestWasCounterKey] ?: defaultWasCounter }

suspend fun SettingsStore.setWasCounter(wasCounter: Boolean) {
    store.edit { it[latestWasCounterKey] = wasCounter }
}
