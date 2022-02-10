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
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.components.settings.modules.notificationHide
import cz.lastaapps.entity.utils.toHours
import cz.lastaapps.entity.utils.toMinutes
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@Composable
fun NotificationHideSelection(
    viewModel: SettingsViewModel, modifier: Modifier = Modifier
) {
    val max = 24 * 60 * 60
    val delay by viewModel.store.notificationHide.collectAsState(null)
    if (delay == null) return

    var slide by remember(delay) { mutableStateOf(1f * delay!!.inWholeSeconds / max) }

    Column(modifier) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                "Notification auto hide after",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            if (slide == 0f) Text("Never") else Text(slide.toSeconds(max).hoursText() + " h")
        }
        Slider(
            value = slide,
            onValueChange = { slide = it },
            steps = max / (15 * 60) - 1,
            onValueChangeFinished = { viewModel.setNotificationHide(slide.toSeconds(max)) },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            )
        )
    }
}

private fun Float.toSeconds(max: Int): Duration = (this * max).toInt().seconds
private fun Duration.hoursText(): String {
    return "%d:%02d".format(toHours(), toMinutes())
}

