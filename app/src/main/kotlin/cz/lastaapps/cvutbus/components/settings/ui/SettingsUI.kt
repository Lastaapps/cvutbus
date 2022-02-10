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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel

@Composable
fun SettingsUI(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    showAbout: Boolean,
    modifier: Modifier = Modifier,
    onAboutClicked: () -> Unit = {},
) {
    Column(
        modifier
            .widthIn(max = 256.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 64.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        DarkMode(settingsViewModel)
        UseDynamicTheme(settingsViewModel, Modifier.fillMaxWidth())

        // disabled until more connections are added
        //PreferredStopPairSelection(settingsViewModel, Modifier.fillMaxWidth())
        PreferredDirectionSelection(settingsViewModel, Modifier.fillMaxWidth())
        TimeShowModeSelection(settingsViewModel)

        NotificationStartSelection(settingsViewModel)
        NotificationHideSelection(settingsViewModel)

        UpdateUI(settingsViewModel)

        Buttons(navController, showAbout, onAboutClicked)
    }
}

