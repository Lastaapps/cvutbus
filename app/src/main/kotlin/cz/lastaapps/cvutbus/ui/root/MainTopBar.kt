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

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.navigation.Dests

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(navController: NavController) {

    val title = stringResource(R.string.ui_top_bar_title)

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }

    var mainPopupExpanded by rememberSaveable { mutableStateOf(false) }

    val list = LocalBackArrowProvider.current

    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        color = colorResource(R.color.ic_launcher_background),
                        shape = RoundedCornerShape(8.dp),
                        contentColor = colorResource(R.color.ic_launcher_foreground),
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.DirectionsBus, null)
                        }
                    }
                }
                Text(title)
            }
        },
        navigationIcon = {
            Row(
                Modifier
                    .animateContentSize()
                    .padding(start = 8.dp)
            ) {
                if (list.isNotEmpty()) {
                    IconButton(onClick = {
                        list.last().invoke()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            stringResource(R.string.ui_top_bar_back_arrow)
                        )
                    }
                }
            }
        },
        actions = {
            Box {
                IconButton({ mainPopupExpanded = !mainPopupExpanded }) {
                    Icon(
                        Icons.Default.MoreVert,
                        stringResource(R.string.ui_top_bar_action_description),
                    )
                }
                TopBarPopup(mainPopupExpanded, { mainPopupExpanded = false }) {
                    navController.navigate(it) {
                        launchSingleTop = true
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun TopBarPopup(
    expanded: Boolean, onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit,
) {
    DropdownMenu(expanded, onDismissRequest, modifier) {
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_privacy)) },
            {
                navigateTo(Dests.Routes.privacyPolicy)
                onDismissRequest()
            })
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_license)) },
            {
                navigateTo(Dests.Routes.license)
                onDismissRequest()
            })
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_osturak)) },
            {
                navigateTo(Dests.Routes.osturak)
                onDismissRequest()
            })
    }
}