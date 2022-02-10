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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.format
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun UpdateUI(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val lastUpdate by remember { viewModel.databaseStore.lastUpdated }.collectAsState(null)
    val info by remember { viewModel.databaseStore.databaseInfo }.collectAsState(null)
    val isRunning by remember { viewModel.updateManager.isRunningFlow() }.collectAsState(null)
    if (lastUpdate == null || info == null || isRunning == null) return

    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            Text("Released: " + info!!.dataReleaseDate.format(dateFormat))
            Text("Valid until: " + info!!.dataValidUntil.format(dateFormat))
            Text("Updated on: " + lastUpdate!!.format(dateFormat))
        }

        if (isRunning == true) {
            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(24.dp))
            }
        } else {
            IconButton(onClick = { viewModel.updateManager.startNow() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Update data")
            }
        }
    }
}
