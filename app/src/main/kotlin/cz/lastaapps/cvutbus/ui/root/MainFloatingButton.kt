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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainFloatingButton() {
    FloatingActionButton(onClick = { println("Fab") }) {
        Icon(Icons.Default.NotificationAdd, contentDescription = "")
    }
}

@Composable
fun MainFloatingBox(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(4.dp),
        shadowElevation = 8.dp,
    ) {
        IconButton(onClick = { println("Almost a fab") }) {
            Icon(Icons.Default.NotificationAdd, contentDescription = "")
        }
    }
}