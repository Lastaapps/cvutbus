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

package cz.lastaapps.cvutbus.pid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import cz.lastaapps.repo.Direction

@Composable
fun PIDIcons(
    pidViewModel: PIDViewModel,
    modifier: Modifier = Modifier,
) {
    val direction by pidViewModel.direction.collectAsState()
    val showCounter by pidViewModel.showCounter.collectAsState()

    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SwitchDirectionIcon(direction == Direction.Inbound) {
            pidViewModel.setDirection(if (direction != Direction.Inbound) Direction.Inbound else Direction.Outbound)
        }
        ShowCounterIcon(showCounter) {
            pidViewModel.setShowCounter(!showCounter)
        }
    }
}

@Composable
private fun SwitchDirectionIcon(direction: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(if (direction) 0f else 180f)

    IconButton(onClick = onClick) {
        Icon(Icons.Default.SwapVert, "Swap directions", Modifier.rotate(rotation))
    }
}

@Composable
private fun ShowCounterIcon(showCounter: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(if (showCounter) 0f else 180f)

    IconButton(onClick = onClick) {
        AnimatedVisibility(showCounter, enter = fadeIn(), exit = fadeOut()) {
            Icon(Icons.Default.HourglassBottom, "Show times", Modifier.rotate(rotation))
        }
        AnimatedVisibility(!showCounter, enter = fadeIn(), exit = fadeOut()) {
            Icon(Icons.Default.Schedule, "Show countdown", Modifier.rotate(rotation + 180f))
        }
    }
}