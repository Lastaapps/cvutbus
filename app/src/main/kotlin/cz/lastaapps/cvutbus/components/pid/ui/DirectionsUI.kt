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

package cz.lastaapps.cvutbus.components.pid.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.lastaapps.cvutbus.components.pid.PIDViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectionsUI(pidViewModel: PIDViewModel, modifier: Modifier = Modifier) {
    val stops by pidViewModel.transportConnection.collectAsState(null)

    if (stops == null) return

    ElevatedCard(
        modifier,
        shape = RoundedCornerShape(8.dp),
    ) {
        val density = LocalDensity.current

        Layout(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            content = {
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
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize(tween())
                    )
                    Text(
                        stops!!.from.name,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize(tween())
                    )
                }

                Icon(Icons.Default.DoubleArrow, null, iconModifier)
            }
        ) { measurable, constrains ->
            val spacing = with(density) { 8.dp.roundToPx() }

            val sizes = measurable.map { it.maxIntrinsicWidth(constrains.maxHeight) }
            val icon1Width = sizes[0]
            val textWidth = sizes[1]
            val icon2Width = sizes[2]

            val placeables = measurable.map { it.measure(constrains) }

            val placeMode: Int
            val width: Int
            val height = placeables.maxOf { it.height }
            when {
                // show both icons
                (icon1Width + textWidth + icon2Width + 2 * spacing) < constrains.maxWidth -> {
                    placeMode = 2
                    width = (icon1Width + textWidth + icon2Width + 2 * spacing)
                }
                // show only one icon
                (icon1Width + textWidth + spacing) < constrains.maxWidth -> {
                    placeMode = 1
                    width = (icon1Width + textWidth + spacing)
                }
                // show only the text
                else -> {
                    placeMode = 0
                    width = textWidth
                }
            }

            layout(width, height) {
                val icon1 = placeables[0]
                val text = placeables[1]
                val icon2 = placeables[2]

                when (placeMode) {
                    2 -> {
                        val textPlaceable = placeables[1]
                        val textOffset = (width - textPlaceable.width) / 2
                        text.also {
                            it.placeRelative(textOffset, (height - it.height) / 2)
                        }
                        icon1.also {
                            it.placeRelative(
                                textOffset - spacing - it.width,
                                (height - it.height) / 2
                            )
                        }
                        icon2.also {
                            it.placeRelative(
                                textOffset + textPlaceable.width + spacing,
                                (height - it.height) / 2
                            )
                        }
                    }
                    1 -> {
                        val total = icon1.width + text.width + spacing
                        val start = (width - total) / 2
                        icon1.also {
                            it.placeRelative(start, (height - it.height) / 2)
                        }
                        text.also {
                            it.placeRelative(
                                start + spacing + icon1.width,
                                (height - it.height) / 2
                            )
                        }
                    }
                    0 -> {
                        text.also {
                            it.placeRelative((width - it.width) / 2, (height - it.height) / 2)
                        }
                    }
                }
            }
        }
    }
}