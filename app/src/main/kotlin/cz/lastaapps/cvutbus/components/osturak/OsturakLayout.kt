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

package cz.lastaapps.cvutbus.components.osturak

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.cvutbus.ui.providers.LocalWindowWidth
import cz.lastaapps.cvutbus.ui.providers.WindowSizeClass
import cz.lastaapps.cvutbus.ui.root.BackArrowAndHandler
import org.lighthousegames.logging.logging

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsturakLayout(
    navController: NavController,
    modifier: Modifier = Modifier,
) {

    @Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> {
            OsturakLayoutCompact(
                modifier = modifier,
            )
        }
        WindowSizeClass.MEDIUM -> {
            OsturakLayoutMedium(
                modifier = modifier,
            )
        }
        WindowSizeClass.EXPANDED -> {
            OsturakLayoutExpanded(
                modifier = modifier,
            )
        }
    }

    BackArrowAndHandler {
        logging("OsturakLayout").i { "Navigating up" }
        navController.navigateUp()
    }
}

@Composable
fun OsturakLayoutCompact(
    modifier: Modifier = Modifier,
) {
    Surface(modifier, shape = RoundedCornerShape(16.dp)) {
        Box(Modifier, contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OsturakText()
                OsturakImages(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun OsturakLayoutMedium(
    modifier: Modifier = Modifier,
) {
    Surface(modifier, shape = RoundedCornerShape(16.dp)) {
        Box(Modifier, contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OsturakText()
                OsturakImages(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun OsturakLayoutExpanded(
    modifier: Modifier = Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(Modifier, contentAlignment = Alignment.Center) {
                OsturakText()
            }
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(Modifier, contentAlignment = Alignment.Center) {
                OsturakImages()
            }
        }
    }
}
