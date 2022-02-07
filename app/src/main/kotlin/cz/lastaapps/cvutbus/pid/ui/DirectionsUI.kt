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

package cz.lastaapps.cvutbus.pid

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun DirectionsUI(pidViewModel: PIDViewModel, modifier: Modifier = Modifier) {
    val stops by pidViewModel.transportConnection.collectAsState(null)

    if (stops != null) {
        Surface(
            modifier,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                Modifier.padding(top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val iconModifier = Modifier
                    .rotate(270f)
                    .size(32.dp)
                Icon(Icons.Default.DoubleArrow, null, iconModifier)

                Column(
                    Modifier.height(IntrinsicSize.Min),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        stops!!.to.name,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize(tween())
                    )
                    Text(
                        stops!!.from.name,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize(tween())
                    )
                }

                Icon(Icons.Default.DoubleArrow, null, iconModifier)
            }
        }
    }
}