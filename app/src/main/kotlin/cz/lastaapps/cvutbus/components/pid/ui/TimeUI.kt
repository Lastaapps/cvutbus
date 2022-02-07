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

import android.text.format.DateFormat
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.lastaapps.cvutbus.api.DatabaseInfo
import cz.lastaapps.cvutbus.components.pid.PIDViewModel
import cz.lastaapps.cvutbus.getRoundedNow
import cz.lastaapps.cvutbus.secondTicker
import cz.lastaapps.entity.utils.CET
import cz.lastaapps.repo.DepartureInfo
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TimeUI(
    pidViewModel: PIDViewModel,
    modifier: Modifier = Modifier,
) {
    val data by pidViewModel.getData().collectAsState(null)

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        when (data) {
            null -> Loading()
            emptyList<DatabaseInfo>() -> NoItems()
            else -> ShowData(data!!, pidViewModel.showCounter.collectAsState().value)
        }
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier)
}

@Composable
private fun NoItems(modifier: Modifier = Modifier) {
    Text(text = "No connections found, try to update offline database", modifier)
}

@Composable
private fun ShowData(
    data: List<DepartureInfo>,
    showCounter: Boolean,
    modifier: Modifier = Modifier
) {
    val now = rememberNow(data)

    val following by remember(data) {
        derivedStateOf { data.take(96 + 1).drop(1).chunked(2) }
    }

    Column(
        modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TimeHeader(showCounter, now, data.first())
        Column(
            Modifier.width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            following.forEach { chunk ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    chunk.forEach { info ->
                        TimeItem(showCounter, now, info, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberNow(data: List<DepartureInfo>): Instant {
    var now by remember { mutableStateOf(getRoundedNow()) }
    LaunchedEffect(data) {
        secondTicker { now = it }
    }
    return now
}

@Composable
private fun TimeHeader(
    showCounter: Boolean,
    now: Instant,
    info: DepartureInfo,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier,
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            Modifier.padding(top = 16.dp, bottom = 16.dp, start = 48.dp, end = 48.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                createTimeText(showCounter, now, info),
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.animateContentSize(tween()),
                textAlign = TextAlign.Center,
            )

            Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
                Text(info.routeShortName, Modifier.padding(6.dp))
            }
        }
    }
}

@Composable
private fun TimeItem(
    showCounter: Boolean,
    now: Instant,
    info: DepartureInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(color = MaterialTheme.colorScheme.secondary, shape = CircleShape) {
            Text(info.routeShortName, Modifier.padding(6.dp))
        }

        Text(
            createTimeText(showCounter, now, info),
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun createTimeText(showCounter: Boolean, now: Instant, info: DepartureInfo): String {
    val context = LocalContext.current
    return remember(showCounter, now, info, context) {
        derivedStateOf {
            if (showCounter) {
                val infoDate = info.dateTime.toInstant(CET)
                val useMinus = infoDate < now
                val duration = if (!useMinus) infoDate - now else (now - infoDate)

                val mainText = if (duration.inWholeHours > 0) {
                    "%d:%02d:%02d".format(
                        duration.inWholeHours,
                        duration.inWholeMinutes % 60,
                        duration.inWholeSeconds % 60
                    )
                } else {
                    "%d:%02d".format(duration.inWholeMinutes % 60, duration.inWholeSeconds % 60)
                }
                if (useMinus) "- $mainText" else mainText
            } else {
                val use24: Boolean = DateFormat.is24HourFormat(context)
                val patter = if (use24) "H:mm" else "h:mm a"
                val formatter = DateTimeFormatter.ofPattern(patter)

                info.dateTime.toJavaLocalDateTime().format(formatter)
            }
        }
    }.value
}

