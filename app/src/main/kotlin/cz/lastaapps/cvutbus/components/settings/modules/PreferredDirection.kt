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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class PreferredDirection(val id: Int) {
    object Inbound : PreferredDirection(0)
    object Outbound : PreferredDirection(1)
    object Remember : PreferredDirection(2)
    object TimeBased : PreferredDirection(3)
    object TimeBasedReversed : PreferredDirection(4)
}

private val preferredDirectionKey = intPreferencesKey("preferred_direction")
private val defaultPreferredDirection = PreferredDirection.TimeBased
private val latestDirectionKey = booleanPreferencesKey("latest_direction")
private val defaultDirection = cz.lastaapps.core.domain.model.Direction.Outbound

val SettingsStore.preferredDirection: Flow<PreferredDirection>
    get() = store.data.map {
        when (it[preferredDirectionKey]) {
            PreferredDirection.Inbound.id -> PreferredDirection.Inbound
            PreferredDirection.Outbound.id -> PreferredDirection.Outbound
            PreferredDirection.Remember.id -> PreferredDirection.Remember
            PreferredDirection.TimeBased.id -> PreferredDirection.TimeBased
            PreferredDirection.TimeBasedReversed.id -> PreferredDirection.TimeBasedReversed
            else -> defaultPreferredDirection
        }
    }

suspend fun SettingsStore.setPreferredDirection(preferred: PreferredDirection) {
    store.edit { it[preferredDirectionKey] = preferred.id }
    log.i { "Storing preferred direction $preferred" }
}


val SettingsStore.latestDirection: Flow<cz.lastaapps.core.domain.model.Direction>
    get() = store.data.map { pref ->
        pref[latestDirectionKey]?.let { cz.lastaapps.core.domain.model.Direction.fromBoolean(it) }
            ?: defaultDirection
    }

suspend fun SettingsStore.setLatestDirection(direction: cz.lastaapps.core.domain.model.Direction) {
    store.edit { it[latestDirectionKey] = direction.toBool }
    log.i { "Storing latest direction $direction" }
}