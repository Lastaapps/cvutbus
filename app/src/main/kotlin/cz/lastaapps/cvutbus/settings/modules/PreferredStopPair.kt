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

package cz.lastaapps.cvutbus.settings.modules

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import cz.lastaapps.cvutbus.settings.SettingsStore
import cz.lastaapps.repo.StopPair
import cz.lastaapps.repo.StopPairs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

sealed class PreferredStopPair(val stopPair: StopPair) {
    data class Remember(private val sp: StopPair = defaultStopPair) :
        PreferredStopPair(sp) // stored as a negative number

    data class SpecifiedStopPair(private val sp: StopPair) :
        PreferredStopPair(sp) //stored as a pair id
}

private val preferredStopKey = intPreferencesKey("preferred_stop_pair")
private val latestStopPairKey = intPreferencesKey("latest_stop_pair")
private val defaultStopPair = StopPairs.strahovDejvicka

val SettingsStore.preferredStopPair: Flow<PreferredStopPair>
    get() = store.data.map { it[preferredStopKey] ?: -1 }.combine(latestStopPair) { key, latest ->
        if (key >= 0) {
            val pair = StopPairs.getPairById(key) ?: error("StopPair $key not found")
            PreferredStopPair.SpecifiedStopPair(pair)
        } else {
            val pair = StopPairs.getPairById(latest) ?: error("Latest StopPair $key not found")
            PreferredStopPair.Remember(pair)
        }
    }

suspend fun SettingsStore.setPreferredStopPair(preferred: PreferredStopPair) {
    store.edit {
        it[preferredStopKey] = when (preferred) {
            is PreferredStopPair.Remember -> -1
            is PreferredStopPair.SpecifiedStopPair -> preferred.stopPair.id
        }
    }
}

val SettingsStore.latestStopPair: Flow<Int>
    get() = store.data.map { it[latestStopPairKey] ?: defaultStopPair.id }

suspend fun SettingsStore.setLatestStopPair(stopPair: StopPair) {
    store.edit { it[latestStopPairKey] = stopPair.id }
}