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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class AppThemeMode(val id: Int) {
    object System : AppThemeMode(0)
    object Light : AppThemeMode(1)
    object Dark : AppThemeMode(2)
}

private val appThemeKey = intPreferencesKey("app_theme")
private val defaultAppTheme = AppThemeMode.System

val SettingsStore.appTheme: Flow<AppThemeMode>
    get() = store.data.map {
        when (it[appThemeKey]) {
            AppThemeMode.Light.id -> AppThemeMode.Light
            AppThemeMode.Dark.id -> AppThemeMode.Dark
            AppThemeMode.System.id -> AppThemeMode.System
            else -> defaultAppTheme
        }
    }

suspend fun SettingsStore.setAppTheme(appTheme: AppThemeMode) {
    store.edit {
        it[appThemeKey] = appTheme.id
    }
}