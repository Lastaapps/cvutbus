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
import cz.lastaapps.cvutbus.settings.SettingsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val dynamicThemeKey = booleanPreferencesKey("dynamic_theme")
private const val defaultDynamicTheme = true

val SettingsStore.dynamicTheme: Flow<Boolean>
    get() = store.data.map { it[dynamicThemeKey] ?: defaultDynamicTheme }

suspend fun SettingsStore.setDynamicTheme(enabled: Boolean) {
    store.edit { it[dynamicThemeKey] = enabled }
}