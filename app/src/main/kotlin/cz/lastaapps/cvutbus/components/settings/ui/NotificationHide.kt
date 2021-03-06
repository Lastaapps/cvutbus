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

package cz.lastaapps.cvutbus.components.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.components.settings.modules.notificationHide
import cz.lastaapps.entity.utils.toHours
import cz.lastaapps.entity.utils.toMinutes
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@Composable
fun NotificationHideSelection(
    viewModel: SettingsViewModel, modifier: Modifier = Modifier
) {
    val max = 6 * 60 * 60
    val steps = max / (15 * 60) - 1
    val computeSteps = steps + 1

    val delay by viewModel.store.notificationHide.collectAsState(null)
    if (delay == null) return

    var slide by remember(delay) { mutableStateOf(1f * delay!!.inWholeSeconds / max) }

    Column(modifier) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.notification_create_hide_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            if (slide == 0f)
                Text(stringResource(R.string.settings_notification_hide_never))
            else
                Text(
                    remember(slide) { slide.toSeconds(max, computeSteps).hoursText() } + " " +
                            stringResource(R.string.settings_notification_hide_hours_abbrev)
                )
        }
        Slider(
            value = slide,
            onValueChange = { slide = it },
            steps = steps,
            onValueChangeFinished = {
                viewModel.setNotificationHide(slide.toSeconds(max, computeSteps))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            )
        )
    }
}

private fun Float.toSeconds(max: Int, steps: Int): Duration {
    val scaled = (this * steps).roundToInt() * 1f / steps
    return (scaled * max).toInt().seconds
}

private fun Duration.hoursText(): String {
    return "%d:%02d".format(toHours(), toMinutes())
}

