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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.cvutbus.api.DatabaseStore
import cz.lastaapps.cvutbus.api.worker.UpdateManager
import cz.lastaapps.cvutbus.components.settings.modules.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val store: SettingsStore,
    val databaseStore: DatabaseStore,
    val updateManager: UpdateManager,
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
}
