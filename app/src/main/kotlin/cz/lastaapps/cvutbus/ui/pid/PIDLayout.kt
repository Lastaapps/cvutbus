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

package cz.lastaapps.cvutbus.ui.pid

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.cvutbus.ui.providers.LocalWindowWidth
import cz.lastaapps.cvutbus.ui.providers.WindowSizeClass

@Composable
fun PIDLayout(
    pidViewModel: PIDViewModel,
    modifier: Modifier = Modifier,
) {
    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> PIDLayoutCompact(pidViewModel, modifier)
        WindowSizeClass.MEDIUM -> PIDLayoutMedium(pidViewModel, modifier)
        WindowSizeClass.EXPANDED -> PIDLayoutExpanded(pidViewModel, modifier)
    }
}

@Composable
private fun PIDLayoutCompact(pidViewModel: PIDViewModel, modifier: Modifier) {
    Column(
        modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DirectionsUI(
            pidViewModel,
            Modifier.fillMaxWidth()
        )
        TimeUI(
            pidViewModel,
            Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        PIDIcons(pidViewModel, Modifier.align(Alignment.Start))
    }
}

@Composable
private fun PIDLayoutMedium(pidViewModel: PIDViewModel, modifier: Modifier) {
    PIDLayoutExpanded(pidViewModel, modifier)
}

@Composable
private fun PIDLayoutExpanded(pidViewModel: PIDViewModel, modifier: Modifier) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            DirectionsUI(
                pidViewModel,
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 312.dp)
            )
            PIDIcons(pidViewModel, Modifier.fillMaxWidth())
        }
        TimeUI(
            pidViewModel,
            Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}