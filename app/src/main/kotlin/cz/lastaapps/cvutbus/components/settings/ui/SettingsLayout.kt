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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.cvutbus.components.AboutUi
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.ui.providers.LocalWindowWidth
import cz.lastaapps.cvutbus.ui.providers.WindowSizeClass
import cz.lastaapps.cvutbus.ui.root.BackArrowAndHandler

@Composable
fun SettingsLayout(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier,
) {
    var showAbout by rememberSaveable { mutableStateOf(false) }
    val onShowAbout: () -> Unit = { showAbout = !showAbout }

    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> SettingsLayoutCompact(
            navController = navController,
            settingsViewModel = settingsViewModel,
            showAbout = showAbout,
            onShowAbout = onShowAbout,
            modifier = modifier,
        )
        WindowSizeClass.MEDIUM -> SettingsLayoutExpanded(
            navController = navController,
            settingsViewModel = settingsViewModel,
            modifier = modifier,
        )
        WindowSizeClass.EXPANDED
        -> SettingsLayoutExpanded(
            navController = navController,
            settingsViewModel = settingsViewModel,
            modifier = modifier,
        )
    }
}

@Composable
private fun SettingsLayoutCompact(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    showAbout: Boolean, onShowAbout: () -> Unit,
    modifier: Modifier,
) {
    if (!showAbout) {
        SettingsUI(
            navController,
            settingsViewModel,
            true,
            onAboutClicked = onShowAbout,
            modifier = modifier
        )
    } else {
        AboutUi(navController, modifier = modifier)
        BackArrowAndHandler(onShowAbout)
    }
}

@Composable
private fun SettingsLayoutExpanded(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingsUI(
            navController, settingsViewModel, false,
            Modifier
                .fillMaxHeight()
                .weight(1f)
        )
        AboutUi(
            navController,
            Modifier
                .fillMaxHeight()
                .weight(1f)
        )
    }
}
