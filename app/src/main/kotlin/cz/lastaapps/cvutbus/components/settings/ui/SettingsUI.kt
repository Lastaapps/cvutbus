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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.components.ReportDialog
import cz.lastaapps.cvutbus.components.sendReport
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.components.settings.modules.*
import cz.lastaapps.cvutbus.navigation.Dests
import cz.lastaapps.cvutbus.ui.theme.AppTheme
import cz.lastaapps.repo.Direction
import cz.lastaapps.repo.StopPair
import cz.lastaapps.repo.StopPairs
import cz.lastaapps.repo.TransportConnection
import kotlin.math.min

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

        Buttons(navController, showAbout, onAboutClicked)
    }
}

@Composable
private fun DarkMode(settingsViewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val mode by settingsViewModel.store.appTheme.collectAsState(initial = null)
    if (mode == null) return

    Column(modifier) {
        Text("App theme", style = MaterialTheme.typography.titleMedium)
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
                        0f -> AppThemeMode.Light
                        1f -> AppThemeMode.Dark
                        else -> AppThemeMode.System
                    }
                )
            },
            steps = 1, modifier = Modifier.fillMaxWidth(),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Light")
            Text("System")
            Text("Dark")
        }
    }
}

@Composable
private fun UseDynamicTheme(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
        return

    val isDynamic by viewModel.store.dynamicTheme.collectAsState(null)

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Color theme", style = MaterialTheme.typography.titleMedium)
        Layout(
            content = {
                AppTheme(darkTheme = false, useCustomTheme = false) {
                    ShowcaseCircle(
                        text = "System",
                        selected = isDynamic == true,
                        onClick = { viewModel.setDynamicTheme(true) },
                    )
                }
                AppTheme(darkTheme = false, useCustomTheme = true) {
                    ShowcaseCircle(
                        text = "ČVUT Bus",
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

    Surface(
        color = surfaceColor,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
        modifier = modifier,
    ) {
        val selectedColor = LocalContentColor.current

        Column(
            Modifier.padding(8.dp),
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

/**
 * Ready for the future where there may be more connection added
 */
@Suppress("unused")
@Composable
private fun PreferredStopPairSelection(
    viewModel: SettingsViewModel, modifier: Modifier = Modifier
) {
    val options = mutableListOf(null as StopPair? to "Remember latest").also { startList ->
        startList.addAll(StopPairs.allStops.map { it to "${it.stop1.name} - ${it.stop2.name}" })
    }
    val onSelected: (StopPair?) -> Unit = { pair ->
        viewModel.setPreferredStopPair(
            if (pair == null) PreferredStopPair.Remember() else PreferredStopPair.SpecifiedStopPair(
                pair
            )
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
    DropDownMenu(
        expanded = expanded,
        onExpanded = { expanded = !expanded },
        label = "Startup Connection",
        options = options,
        selected = selectedIndex,
        onItemSelected = onSelected,
        modifier = modifier,
    )
}

@Composable
private fun PreferredDirectionSelection(
    viewModel: SettingsViewModel, modifier: Modifier = Modifier
) {
    val stopPair by viewModel.store.preferredStopPair.collectAsState(initial = null)
    if (stopPair == null) return

    val inboundInfo = TransportConnection.fromStopPair(stopPair!!.stopPair, Direction.Inbound)
    val outboundInfo = TransportConnection.fromStopPair(stopPair!!.stopPair, Direction.Outbound)

    val options = listOf(
        PreferredDirection.Inbound to "to ${inboundInfo.to.name}",
        PreferredDirection.Outbound to "to ${outboundInfo.to.name}",
        PreferredDirection.Remember to "Remember latest",
        PreferredDirection.TimeBased to "Change at noon",
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
    DropDownMenu(
        expanded = expanded,
        onExpanded = { expanded = !expanded },
        label = "Startup direction",
        options = options,
        selected = selectedIndex,
        onItemSelected = onSelected,
        modifier = modifier,
    )
}

@Composable
private fun TimeShowModeSelection(
    viewModel: SettingsViewModel, modifier: Modifier = Modifier
) {
    val options = listOf(
        TimeShowMode.Countdown to "Countdown",
        TimeShowMode.Remember to "Remember",
        TimeShowMode.Time to "Time",
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
    DropDownMenu(
        expanded = expanded,
        onExpanded = { expanded = !expanded },
        label = "Startup time mode",
        options = options,
        selected = selectedIndex,
        onItemSelected = onSelected,
        modifier = modifier,
    )
}

@Composable
private fun Buttons(
    navController: NavController,
    showAbout: Boolean, onAboutClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val uriHandler = LocalUriHandler.current

        Button(
            onClick = { navController.navigate(Dests.Routes.privacyPolicy) },
            Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.settings_button_privacy_policy),
                textAlign = TextAlign.Center
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
        ) {
            AboutButton(
                showAbout = showAbout,
                onAboutClicked = onAboutClicked,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )

            Button(
                onClick = { uriHandler.openUri("https://play.google.com/store/apps/details?id=cz.lastaapps.cvutbus") },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) { Text(stringResource(R.string.settings_button_rate), textAlign = TextAlign.Center) }
        }

        ReportButton(Modifier.fillMaxWidth())
    }
}

@Composable
private fun ReportButton(modifier: Modifier = Modifier) {
    var shown by rememberSaveable { mutableStateOf(false) }

    Button(onClick = { shown = true }, modifier) {
        Text(stringResource(R.string.settings_button_report), textAlign = TextAlign.Center)
    }

    val context = LocalContext.current
    ReportDialog(shown, { shown = false }) {
        sendReport(context, it)
        shown = false
    }
}

@Composable
private fun AboutButton(
    showAbout: Boolean, onAboutClicked: () -> Unit, modifier: Modifier = Modifier
) {
    if (!showAbout) return

    Button(onClick = onAboutClicked, modifier) {
        Text(stringResource(R.string.settings_button_about))
    }
}
