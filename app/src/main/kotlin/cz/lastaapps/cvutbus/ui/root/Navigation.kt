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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import cz.lastaapps.cvutbus.components.license.ui.LicenseLayout
import cz.lastaapps.cvutbus.components.osturak.OsturakLayout
import cz.lastaapps.cvutbus.components.pid.PIDViewModel
import cz.lastaapps.cvutbus.components.pid.ui.PIDLayout
import cz.lastaapps.cvutbus.components.privacy.PrivacyDialogContent
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.components.settings.ui.SettingsLayout
import cz.lastaapps.cvutbus.navigation.Dests
import kotlinx.coroutines.flow.collectLatest
import org.lighthousegames.logging.logging

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    pidViewModel: PIDViewModel,
    settingsViewModel: SettingsViewModel,
) {
    LaunchedEffect(navHostController) {
        val log = logging("Navigation")
        navHostController.currentBackStackEntryFlow.collectLatest {
            log.i { "Navigation to ${it.destination.route}" }
        }
    }
    val contentModifier = Modifier.fillMaxSize()
    AnimatedNavHost(navHostController, startDestination = Dests.Routes.starting) {
        composable(Dests.Routes.pid) {
            PIDLayout(pidViewModel, contentModifier)
        }
        composable(Dests.Routes.settings) {
            SettingsLayout(navHostController, settingsViewModel, contentModifier)
        }
        composable(Dests.Routes.osturak) {
            OsturakLayout(navHostController)
        }
        composable(Dests.Routes.license) {
            LicenseLayout(navHostController, contentModifier)
        }
        dialog(Dests.Routes.privacyPolicy) {
            PrivacyDialogContent(false)
        }
    }
}
