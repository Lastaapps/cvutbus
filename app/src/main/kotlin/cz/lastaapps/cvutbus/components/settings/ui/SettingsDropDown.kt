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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SettingsDropDown(
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    label: String?,
    options: List<Pair<T, String>>,
    selected: Int,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    defaultItem: String? = null,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (label != null)
            Text(label, style = MaterialTheme.typography.titleMedium)

        //TODO try to rework in next compose version
        var exp by remember { mutableStateOf(false) }
        LaunchedEffect(expanded) { exp = expanded }

        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = exp,
            onExpandedChange = { onExpanded(!exp) },
        ) {
            TextField(
                readOnly = true,
                value = options.getOrNull(selected)?.second ?: defaultItem!!,
                onValueChange = { },
                //label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(exp) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = exp,
                onDismissRequest = { onExpanded(false) }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(option.first)
                            onExpanded(false)
                        },
                        text = { Text(text = option.second) }
                    )
                }
            }
        }
    }
}
