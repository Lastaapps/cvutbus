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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import cz.lastaapps.cvutbus.*
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.components.settings.modules.NotificationStartup
import cz.lastaapps.cvutbus.components.settings.modules.notificationStartTime
import cz.lastaapps.cvutbus.components.settings.modules.notificationStartup
import cz.lastaapps.cvutbus.components.settings.modules.notificationWorkDaysOnly
import cz.lastaapps.entity.utils.toHours
import cz.lastaapps.entity.utils.toMinutes
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun NotificationStartSelection(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val mode by viewModel.store.notificationStartup.collectAsState(null)
    val nextAlarm by viewModel.getAlarms().collectAsState(null)
    val time by viewModel.store.notificationStartTime.collectAsState(null)
    val workDays by viewModel.store.notificationWorkDaysOnly.collectAsState(null)
    if (mode == null || time == null || workDays == null) return

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.settings_notification_start_title),
            style = MaterialTheme.typography.titleMedium,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ModeDropdown(
                mode!!,
                { viewModel.setNotificationStartMode(it) },
                nextAlarm, time!!,
                Modifier
                    .weight(1f)
                    .animateContentSize(),
            )
            when (mode) {
                NotificationStartup.AlarmBased -> {
                    UpdateAlarm(viewModel)
                }
                NotificationStartup.TimeBased -> {
                    SelectTime(time!!, { viewModel.setNotificationStartTime(it) })
                }
                else -> {}
            }
        }
        if (mode != NotificationStartup.Disabled) {
            WeekDaysOnly(
                workDays!!,
                onClick = { viewModel.setNotificationWorkDaysOnly(!workDays!!) },
                Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ModeDropdown(
    mode: NotificationStartup,
    onMode: (NotificationStartup) -> Unit,
    nextAlarm: Duration?,
    time: Duration,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val nextAlarmString =
        remember(nextAlarm, context) { nextAlarm?.toLocalTime()?.localizedFormat(context) }
            ?: stringResource(R.string.settings_notification_start_alarm_none)
    val timeString = remember(time, context) { time.toLocalTime().localizedFormat(context) }

    val options = listOf(
        NotificationStartup.Disabled to
                stringResource(R.string.settings_notification_start_disabled),
        NotificationStartup.AlarmBased to
                stringResource(R.string.settings_notification_start_alarm).format(nextAlarmString),
        NotificationStartup.TimeBased to
                stringResource(R.string.settings_notification_start_time).format(timeString),
    )
    val selected = options.map { it.first }.indexOf(mode)

    var expanded by rememberSaveable { mutableStateOf(false) }
    SettingsDropDown(
        expanded = expanded,
        onExpanded = { expanded = !expanded },
        label = null,
        options = options,
        selected = selected,
        onItemSelected = onMode,
        modifier = modifier,
    )
}

@Composable
private fun UpdateAlarm(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    IconButton(onClick = { viewModel.updateNextAlarm() }, modifier) {
        Icon(
            Icons.Default.Refresh,
            stringResource(R.string.settings_notification_start_alarm_refresh)
        )
    }
}

@Composable
private fun SelectTime(time: Duration, onTime: (Duration) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var pickerShown by rememberSaveable { mutableStateOf(false) }

    val picker = remember(context) {
        with(MaterialTimePicker.Builder()) {
            val isSystem24Hour = context.uses24Hour()
            val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
            setTimeFormat(clockFormat)
            setHour(time.toHours())
            setMinute(time.toMinutes())
            setTitleText(context.getString(R.string.settings_notification_start_picker_title))
            build()
        }
            .apply {
                addOnPositiveButtonClickListener {
                    onTime((hour * 60 + minute).minutes)
                }
                addOnDismissListener { pickerShown = false }
            }
    }

    IconButton(onClick = { pickerShown = !pickerShown }, modifier) {
        Icon(
            Icons.Default.Schedule,
            stringResource(R.string.settings_notification_start_picker_button_description),
        )
    }


    DisposableEffect(pickerShown) {
        if (pickerShown) {
            picker.show((context as MainActivity).supportFragmentManager, "time_picker")
        }

        onDispose {
            if (picker.isVisible && picker.isAdded && !picker.isStateSaved)
                picker.dismiss()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeekDaysOnly(workDays: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.clickable { onClick() }, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = workDays, onCheckedChange = { onClick() })
        Text(text = stringResource(R.string.settings_notification_start_workdays_text))
    }
}
