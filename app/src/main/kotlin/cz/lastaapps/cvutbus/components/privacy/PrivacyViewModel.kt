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

package cz.lastaapps.cvutbus.components.privacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PrivacyViewModel @Inject constructor(
    private val store: PrivacyStore,
) : ViewModel() {

    companion object {
        private val log = logging()
    }

    val shouldShow = store.approved.map { it == null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun onApprove() {
        viewModelScope.launch {
            log.i { "Approving privacy policy" }
            store.setApproved(LocalDate.now())
        }
    }
}