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

package cz.lastaapps.cvutbus.components.settings.ui

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.components.settings.modules.*
import cz.lastaapps.repo.Direction
import cz.lastaapps.repo.StopPair
import cz.lastaapps.repo.StopPairs
import cz.lastaapps.repo.TransportConnection

/**
 * Ready for the future where there may be more connection added
 */
@Suppress("unused")
@Composable
fun PreferredStopPairSelection(
    viewModel: SettingsViewModel, modifier: Modifier = Modifier
) {
    val options = mutableListOf(null as StopPair? to "Remember latest").also { startList ->
        startList.addAll(StopPairs.allStops.map { it to "${it.stop1.name} - ${it.stop2.name}" })
    }
    val onSelected: (StopPair?) -> Unit = { pair ->
        viewModel.setPreferredStopPair(
            if (pair == null) PreferredStopPair.Remember() else PreferredStopPair.SpecifiedStopPair(
                pair
            )
        )
    }
    val selectedItem by viewModel.store.preferredStopPair.collectAsState(initial = null)
    val selectedIndex by remember(selectedItem) {
        derivedStateOf {
            when (selectedItem) {
                is PreferredStopPair.SpecifiedStopPair ->
                    options.map { it.first }.indexOf(selectedItem?.stopPair)
                else -> 0
            }
        }
    }

    var expanded by rememberSaveable { mutableStateOf(false) }
    DropDownMenu(
        expanded = expanded,
        onExpanded = { expanded = !expanded },
        label = "Startup Connection",
        options = options,
        selected = selectedIndex,
        onItemSelected = onSelected,
        modifier = modifier,
    )
}

@Composable
fun PreferredDirectionSelection(
    viewModel: SettingsViewModel, modifier: Modifier = Modifier
) {
    val stopPair by viewModel.store.preferredStopPair.collectAsState(initial = null)
    if (stopPair == null) return

    val inboundInfo = TransportConnection.fromStopPair(stopPair!!.stopPair, Direction.Inbound)
    val outboundInfo = TransportConnection.fromStopPair(stopPair!!.stopPair, Direction.Outbound)

    val options = listOf(
        PreferredDirection.Inbound to "to ${inboundInfo.to.name}",
        PreferredDirection.Outbound to "to ${outboundInfo.to.name}",
        PreferredDirection.Remember to "Remember latest",
        PreferredDirection.TimeBased to "Change at noon",
    )
    val onSelected: (PreferredDirection) -> Unit = { direction ->
        viewModel.setPreferredDirection(direction)
    }
    val selectedItem by viewModel.store.preferredDirection.collectAsState(initial = null)
    if (selectedItem == null) return
    val selectedIndex by remember(selectedItem) {
        derivedStateOf { options.map { it.first }.indexOf(selectedItem!!) }
    }

    var expanded by rememberSaveable { mutableStateOf(false) }
    DropDownMenu(
        expanded = expanded,
        onExpanded = { expanded = !expanded },
        label = "Startup direction",
        options = options,
        selected = selectedIndex,
        onItemSelected = onSelected,
        modifier = modifier,
    )
}

@Composable
fun TimeShowModeSelection(
    viewModel: SettingsViewModel, modifier: Modifier = Modifier
) {
    val options = listOf(
        TimeShowMode.Countdown to "Countdown",
        TimeShowMode.Remember to "Remember",
        TimeShowMode.Time to "Time",
    )
    val onSelected: (TimeShowMode) -> Unit = { mode ->
        viewModel.setTimeShowMode(mode)
    }
    val selectedItem by viewModel.store.timeShowMode.collectAsState(initial = null)
    if (selectedItem == null) return
    val selectedIndex by remember(selectedItem) {
        derivedStateOf { options.map { it.first }.indexOf(selectedItem!!) }
    }

    var expanded by rememberSaveable { mutableStateOf(false) }
    DropDownMenu(
        expanded = expanded,
        onExpanded = { expanded = !expanded },
        label = "Startup time mode",
        options = options,
        selected = selectedIndex,
        onItemSelected = onSelected,
        modifier = modifier,
    )
}
