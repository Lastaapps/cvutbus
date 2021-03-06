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

package cz.lastaapps.cvutbus.components.pid.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.components.battery.BatteryChoiceDialog
import cz.lastaapps.cvutbus.components.battery.shouldShowBattery
import cz.lastaapps.cvutbus.components.pid.PIDViewModel
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.repo.Direction
import org.lighthousegames.logging.logging

@Composable
fun PIDIcons(
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
    isLarge: Boolean,
    modifier: Modifier = Modifier,
) {
    val direction by pidViewModel.direction.collectAsState()
    val showCounter by pidViewModel.showCounter.collectAsState()
    val battery = shouldShowBattery(settingsViewModel)
    var batteryDialog by rememberSaveable { mutableStateOf(false) }

    val log = remember { logging("PIDIcons") }
    val onDirection: () -> Unit = {
        log.i { "Switching direction from $direction" }
        pidViewModel.setDirection(if (direction != Direction.Inbound) Direction.Inbound else Direction.Outbound)
    }
    val onCounter: () -> Unit = {
        log.i { "Switching time mode from $showCounter" }
        pidViewModel.setShowCounter(!showCounter)
    }
    val onBattery: () -> Unit = {
        log.i { "Opening battery" }
        batteryDialog = true
    }

    if (isLarge) {
        Column(
            modifier,
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                Modifier.clickable(onClick = onDirection),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SwitchDirectionIcon(direction == Direction.Inbound, onDirection)
                Text(stringResource(R.string.pid_button_direction))
            }
            Row(
                Modifier.clickable(onClick = onCounter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShowCounterIcon(showCounter, onCounter)
                Text(stringResource(R.string.pid_button_time_mode))
            }
            Row(
                Modifier.clickable(onClick = onCounter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BatteryOptimization(battery, onBattery)
                Text(stringResource(R.string.pid_button_battery))
            }
        }
    } else {
        Row(
            modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SwitchDirectionIcon(direction == Direction.Inbound, onDirection)
            ShowCounterIcon(showCounter, onCounter)
            BatteryOptimization(battery, onBattery)
        }
    }

    ShowBatteryDialog(batteryDialog, settingsViewModel) { batteryDialog = false }
}

@Composable
private fun SwitchDirectionIcon(direction: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(if (direction) 0f else 180f)

    IconButton(onClick = onClick) {
        Icon(
            Icons.Default.SwapVert,
            stringResource(R.string.pid_icon_description_direction),
            Modifier.rotate(rotation)
        )
    }
}

@Composable
private fun ShowCounterIcon(showCounter: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(if (showCounter) 0f else 180f)

    IconButton(onClick = onClick) {
        Crossfade(targetState = showCounter) {
            if (it) {
                Icon(
                    Icons.Default.HourglassBottom,
                    stringResource(R.string.pid_icon_description_to_time),
                    Modifier.rotate(rotation)
                )
            } else {

                Icon(
                    Icons.Default.Schedule,
                    stringResource(R.string.pid_icon_description_to_counter),
                    Modifier.rotate(rotation + 180f)
                )
            }
        }
    }
}

@Composable
private fun BatteryOptimization(shouldShow: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(visible = shouldShow, enter = fadeIn(), exit = fadeOut()) {
        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.BatteryAlert,
                stringResource(R.string.pid_icon_description_battery),
            )
        }
    }
}

@Composable
private fun ShowBatteryDialog(
    shown: Boolean,
    settingsViewModel: SettingsViewModel,
    onDismissRequest: () -> Unit,
) {
    if (!shown) return

    BatteryChoiceDialog(settingsViewModel, onDismissRequest)
}