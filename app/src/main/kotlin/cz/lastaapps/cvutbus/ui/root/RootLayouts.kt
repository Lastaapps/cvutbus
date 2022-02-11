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

package cz.lastaapps.cvutbus.ui.root

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.lastaapps.cvutbus.components.pid.PIDViewModel
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayoutCompact(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
    content: @Composable () -> Unit,
) {
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            MainTopBar(navController)
        },
        bottomBar = {
            MainBottomNav(navController)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            MainFloatingButton()
        },
    ) { insets ->
        Box(
            Modifier
                .padding(insets)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayoutMedium(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
    content: @Composable () -> Unit,
) {
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            MainTopBar(navController)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { insets ->
        Row(
            Modifier
                .padding(insets)
                .fillMaxSize()
        ) {
            MainNavRail(navController = navController, Modifier.fillMaxHeight())
            Box(
                Modifier
                    .padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
                    .fillMaxSize()
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayoutExpanded(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
    content: @Composable () -> Unit,
) {
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            MainTopBar(navController)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { insets ->
        Row(
            Modifier
                .padding(insets)
                .fillMaxSize()
        ) {
            MainNavRail(navController = navController, Modifier.fillMaxHeight())
            Box(
                Modifier
                    .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxSize()
            ) {
                content()
            }
        }
    }
}
