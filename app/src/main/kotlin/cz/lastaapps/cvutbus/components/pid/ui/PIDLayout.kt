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

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import cz.lastaapps.cvutbus.components.pid.PIDViewModel
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.ui.providers.LocalWindowWidth
import cz.lastaapps.cvutbus.ui.providers.WindowSizeClass

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PIDLayout(
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    if (!pidViewModel.isReady.collectAsState().value)
        return

    val pagerState = pidViewModel.pagerState
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            pidViewModel.onPage(page)
        }
    }

    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> PIDLayoutCompact(pidViewModel, settingsViewModel, modifier)
        WindowSizeClass.MEDIUM -> PIDLayoutMedium(pidViewModel, settingsViewModel, modifier)
        WindowSizeClass.EXPANDED -> PIDLayoutExpanded(pidViewModel, settingsViewModel, modifier)
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun PIDLayoutCompact(
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier
) {
    val pagerState = pidViewModel.pagerState
    Box(modifier) {
        HorizontalPager(
            pidViewModel.pageCount, state = pagerState,
            modifier = Modifier,
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DirectionsUI(
                    page, pidViewModel,
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
                TimeUI(
                    page, pidViewModel, settingsViewModel,
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
        ElevatedCard(Modifier.align(Alignment.BottomStart)) {
            PIDIcons(pidViewModel, settingsViewModel, false, Modifier.padding(4.dp))
        }
    }
}

@Composable
private fun PIDLayoutMedium(
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier
) {
    PIDLayoutExpanded(pidViewModel, settingsViewModel, modifier)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun PIDLayoutExpanded(
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier
) {
    val pagerState = pidViewModel.pagerState
    HorizontalPager(
        pidViewModel.pageCount, state = pagerState,
        modifier = modifier
    ) { page ->
        Row(
            Modifier.padding(horizontal = 4.dp),
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
                    page, pidViewModel,
                    Modifier
                        .fillMaxWidth()
                        .widthIn(max = 312.dp)
                )
                PIDIcons(pidViewModel, settingsViewModel, true, Modifier.fillMaxWidth())
            }
            TimeUI(
                page, pidViewModel, settingsViewModel,
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}