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

package cz.lastaapps.cvutbus.components.battery

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.components.settings.modules.batteryDismissed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

@Composable
fun shouldShowBattery(settingsViewModel: SettingsViewModel): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false

    val context = LocalContext.current
    var state by remember { mutableStateOf(false) }

    LaunchedEffect(context, settingsViewModel) {
        val ignoring = isNotIgnoringBatteryOptimizationsFlow(context)
        settingsViewModel.store.batteryDismissed.combine(ignoring) { dismissed, isNotIgnoring ->
            !dismissed && isNotIgnoring
        }.collectLatest {
            state = it
        }
    }

    return state
}

@Composable
fun isNotBatteryOptimized(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false

    val context = LocalContext.current
    return remember(context) {
        isNotIgnoringBatteryOptimizationsFlow(context)
    }.collectAsState(initial = false).value
}

private fun isNotIgnoringBatteryOptimizationsFlow(context: Context): Flow<Boolean> =
    flow {
        while (true) {
            emit(isNotIgnoringBatteryOptimizations(context))
            delay(1000)
        }
    }

private fun isNotIgnoringBatteryOptimizations(context: Context): Boolean {
    val pm = context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
    val name = context.applicationContext.packageName
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return !pm.isIgnoringBatteryOptimizations(name)
    }
    return false
}