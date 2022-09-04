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
import androidx.compose.ui.res.stringResource
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.components.settings.modules.*
import cz.lastaapps.database.domain.model.Direction
import cz.lastaapps.database.domain.model.StopPair
import cz.lastaapps.database.domain.model.StopPairs
import cz.lastaapps.database.domain.model.TransportConnection

/**
 * Ready for the future where there may be more connection added
 */
@Composable
fun PreferredStopPairSelection(
    viewModel: SettingsViewModel, modifier: Modifier = Modifier
) {
    val options =
        mutableListOf(
            null as StopPair? to stringResource(R.string.settings_preferred_stops_remember)
        ).also { startList ->
            startList.addAll(StopPairs.allStops.map { it to "${it.stop1.name} - ${it.stop2.name}" })
        }
    val onSelected: (StopPair?) -> Unit = { pair ->
        viewModel.setPreferredStopPair(
            if (pair == null) PreferredStopPair.Remember()
            else PreferredStopPair.SpecifiedStopPair(pair)
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
    SettingsDropDown(
        expanded = expanded,
        onExpanded = { expanded = it },
        label = stringResource(R.string.settings_preferred_stops_label),
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
        PreferredDirection.Inbound to
                stringResource(R.string.settings_preferred_direction_to).format(inboundInfo.to.name),
        PreferredDirection.Outbound to
                stringResource(R.string.settings_preferred_direction_to).format(outboundInfo.to.name),
        PreferredDirection.Remember to
                stringResource(R.string.settings_preferred_direction_remember),
        PreferredDirection.TimeBased to
                stringResource(R.string.settings_preferred_direction_noon),
        PreferredDirection.TimeBasedReversed to
                stringResource(R.string.settings_preferred_direction_noon_reversed),
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
    SettingsDropDown(
        expanded = expanded,
        onExpanded = { expanded = it },
        label = stringResource(R.string.settings_preferred_direction_title),
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
        TimeShowMode.Remember to stringResource(R.string.settings_preferred_time_mode_remember),
        TimeShowMode.Countdown to stringResource(R.string.settings_preferred_time_mode_countdown),
        TimeShowMode.Time to stringResource(R.string.settings_preferred_time_mode_time),
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
    SettingsDropDown(
        expanded = expanded,
        onExpanded = { expanded = it },
        label = stringResource(R.string.settings_preferred_time_mode_title),
        options = options,
        selected = selectedIndex,
        onItemSelected = onSelected,
        modifier = modifier,
    )
}
