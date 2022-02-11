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

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.components.settings.modules.AppThemeMode
import cz.lastaapps.cvutbus.components.settings.modules.appTheme
import cz.lastaapps.cvutbus.components.settings.modules.dynamicTheme
import cz.lastaapps.cvutbus.ui.theme.AppTheme
import kotlin.math.min

@Composable
fun DarkMode(settingsViewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val mode by settingsViewModel.store.appTheme.collectAsState(initial = null)
    if (mode == null) return

    Column(modifier) {
        Text(
            stringResource(R.string.settings_app_theme_title),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Icon(Icons.Default.LightMode, null)
            Icon(Icons.Default.BrightnessMedium, null)
            Icon(Icons.Default.DarkMode, null)
        }

        var slide by remember(mode) {
            mutableStateOf(
                when (mode!!) {
                    AppThemeMode.Light -> 0f
                    AppThemeMode.System -> .5f
                    AppThemeMode.Dark -> 1f
                }
            )
        }
        Slider(
            value = slide, onValueChange = { slide = it },
            onValueChangeFinished = {
                settingsViewModel.setAppTheme(
                    when (slide) {
                        in 0f..0.25f -> AppThemeMode.Light
                        in 0.75f..1f -> AppThemeMode.Dark
                        else -> AppThemeMode.System
                    }
                )
            },
            steps = 1, modifier = Modifier.fillMaxWidth(),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.settings_app_theme_light))
            Text(stringResource(R.string.settings_app_theme_system))
            Text(stringResource(R.string.settings_app_theme_dark))
        }
    }
}

@Composable
fun UseDynamicTheme(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
        return

    val isDynamic by viewModel.store.dynamicTheme.collectAsState(null)

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.settings_dynamic_theme_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Layout(
            content = {
                AppTheme(darkTheme = false, useCustomTheme = false) {
                    ShowcaseCircle(
                        text = stringResource(R.string.settings_dynamic_theme_system),
                        selected = isDynamic == true,
                        onClick = { viewModel.setDynamicTheme(true) },
                    )
                }
                AppTheme(darkTheme = false, useCustomTheme = true) {
                    ShowcaseCircle(
                        text = stringResource(R.string.settings_dynamic_theme_app),
                        selected = isDynamic == false,
                        onClick = { viewModel.setDynamicTheme(false) },
                    )
                }
            },
            Modifier.fillMaxWidth(),
        ) { measurableList, constrains ->

            val itemConstrains = with(constrains) {
                val newMaxWidth = maxWidth / measurableList.size
                copy(minWidth = min(minWidth, newMaxWidth), maxWidth = newMaxWidth)
            }

            val itemHeight =
                measurableList.map { it.maxIntrinsicHeight(itemConstrains.maxWidth) }.maxOf { it }
            val itemWidth = measurableList.map { it.maxIntrinsicWidth(itemHeight) }.maxOf { it }

            val measureConstrain = Constraints(itemWidth, itemWidth, itemHeight, itemHeight)
            val placeableList = measurableList.map { it.measure(measureConstrain) }

            layout(constrains.maxWidth, itemHeight) {
                val totalSpace = constrains.maxWidth - placeableList.size * itemWidth
                val space = totalSpace / (placeableList.size + 1)
                placeableList.forEachIndexed { index, placeable ->
                    placeable.placeRelative(space * (index + 1) + index * itemWidth, 0)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowcaseCircle(
    text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val surfaceColor = if (selected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface

    val interaction = remember { MutableInteractionSource() }
    Card(
        containerColor = surfaceColor,
        shape = RoundedCornerShape(8.dp),
        interactionSource = interaction,
        modifier = modifier.clickable(interaction, null, onClick = onClick),
    ) {
        val selectedColor = LocalContentColor.current

        Column(
            Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Canvas(Modifier.size(48.dp)) {
                drawArc(primary, 90f, 180F, useCenter = true)
                drawArc(secondary, 270f, 90f, useCenter = true)
                drawArc(tertiary, 0f, 90f, useCenter = true)
                drawCircle(selectedColor, style = Stroke(8f))
            }
            Text(text)
        }
    }
}
