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

package cz.lastaapps.cvutbus.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.cvutbus.settings.modules.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val store: SettingsStore,
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
}
