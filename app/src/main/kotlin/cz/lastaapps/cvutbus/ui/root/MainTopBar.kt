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

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar() {

    val title = "ČVUT Bus"

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }

    SmallTopAppBar(
        title = { Text(title) },
        navigationIcon = {},
        actions = {
            /*IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }*/
        },
        scrollBehavior = scrollBehavior
    )
}