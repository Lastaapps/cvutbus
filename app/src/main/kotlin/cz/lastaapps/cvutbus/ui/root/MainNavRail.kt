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

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.navigation.Dests
import cz.lastaapps.cvutbus.navigation.routesEquals

data class NavItem(
    @StringRes val label: Int,
    val dest: String,
    val icon: ImageVector,
)

val navItems = listOf(
    NavItem(R.string.nav_pid, Dests.Routes.pid, Icons.Filled.DirectionsBus),
    NavItem(R.string.nav_settings, Dests.Routes.settings, Icons.Filled.Settings),
)

@Composable
fun MainNavRail(
    navController: NavController,
    modifier: Modifier = Modifier,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route

    NavigationRail(modifier) {
        MainFloatingBox(Modifier.padding(bottom = 16.dp))

        navItems.forEach { item ->
            val selected = route?.routesEquals(item.dest) ?: false

            NavigationRailItem(
                icon = { Icon(item.icon, null) },
                label = { Text(stringResource(item.label)) },
                selected = selected,
                onClick = {
                    navController.navigate(item.dest) {
                        launchSingleTop = true
                        popUpTo(Dests.Routes.starting) {
                            saveState = true
                            inclusive = false
                        }
                    }
                },
                alwaysShowLabel = false
            )
        }
    }
}

