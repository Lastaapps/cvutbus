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

package cz.lastaapps.cvutbus.ui.root

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import cz.lastaapps.cvutbus.navigation.Dests
import cz.lastaapps.cvutbus.pid.PIDViewModel
import cz.lastaapps.cvutbus.pid.ui.PIDLayout
import cz.lastaapps.cvutbus.settings.SettingsViewModel
import cz.lastaapps.cvutbus.settings.modules.AppThemeMode
import cz.lastaapps.cvutbus.settings.modules.appTheme
import cz.lastaapps.cvutbus.settings.modules.dynamicTheme
import cz.lastaapps.cvutbus.settings.ui.SettingsLayout
import cz.lastaapps.cvutbus.ui.WithConnectivity
import cz.lastaapps.cvutbus.ui.providers.*
import cz.lastaapps.cvutbus.ui.theme.AppTheme

@Composable
fun AppLayout(
    activity: Activity,
    viewModelStoreOwner: ViewModelStoreOwner,
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
    onThemeReady: () -> Unit,
) {

    val appTheme by settingsViewModel.store.appTheme.collectAsState(initial = null)
    val dynamic by settingsViewModel.store.dynamicTheme.collectAsState(initial = null)
    if (appTheme == null || dynamic == null)
        return

    SideEffect { onThemeReady() }

    val expected = isSystemInDarkTheme()
    val darkState by remember(appTheme, expected) {
        derivedStateOf {
            when (appTheme) {
                AppThemeMode.Dark -> true
                AppThemeMode.Light -> false
                AppThemeMode.System -> expected
                null -> false
            }
        }
    }

    AppTheme(useCustomTheme = !dynamic!!, darkTheme = darkState, updateSystemBars = true) {
        ApplyProviders(activity = activity, viewModelStoreOwner = viewModelStoreOwner) {
            AppContent(pidViewModel, settingsViewModel)
        }
    }
}

@Composable
private fun ApplyProviders(
    activity: Activity, viewModelStoreOwner: ViewModelStoreOwner, content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalActivityViewModelOwner provides viewModelStoreOwner) {
        WithLocalWindowSizes(activity) {
            WithFoldingFeature(activity) {
                ProvideWindowInsets {
                    WithConnectivity {
                        content()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AppContent(
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val navController = rememberAnimatedNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val content: @Composable () -> Unit = {
        AppNavigation(
            navHostController = navController, snackbarHostState = snackbarHostState,
            pidViewModel = pidViewModel, settingsViewModel = settingsViewModel,
        )
    }

    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> AppLayoutCompact(
            navController = navController, snackbarHostState = snackbarHostState,
            pidViewModel = pidViewModel, settingsViewModel = settingsViewModel,
            content = content,
        )
        WindowSizeClass.MEDIUM -> AppLayoutMedium(
            navController = navController, snackbarHostState = snackbarHostState,
            pidViewModel = pidViewModel, settingsViewModel = settingsViewModel,
            content = content,
        )
        WindowSizeClass.EXPANDED -> AppLayoutExpanded(
            navController = navController, snackbarHostState = snackbarHostState,
            pidViewModel = pidViewModel, settingsViewModel = settingsViewModel,
            content = content,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AppNavigation(
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
) {
    AnimatedNavHost(navHostController, startDestination = Dests.Routes.starting) {
        composable(Dests.Routes.pid) {
            PIDLayout(pidViewModel, Modifier.fillMaxSize())
        }
        composable(Dests.Routes.settings) {
            SettingsLayout(settingsViewModel, Modifier.fillMaxSize())
        }
    }
}
