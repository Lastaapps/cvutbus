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
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.notification.WorkerUtils
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

@Composable
fun MainFloatingButton() {
    FloatingActionButton(onClick = onFabAction()) {
        FloatingIcon()
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
        IconButton(onClick = onFabAction()) {
            FloatingIcon()
        }
    }
}

@Composable
private fun FloatingIcon(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val serviceRunning by remember(context) {
        WorkerUtils(context).isRunningFlow()
    }.collectAsState(initial = null)

    if (serviceRunning == false)
        Icon(
            Icons.Default.NotificationAdd,
            stringResource(R.string.ui_fab_notification_show),
            modifier,
        )
    else
        Icon(
            Icons.Default.NotificationsOff,
            stringResource(R.string.ui_fab_notification_dismiss),
            modifier,
        )
}

@Composable
private fun onFabAction(): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return {
        scope.launch {
            logging("Fab").i { "Fab pressed" }
            WorkerUtils(context).toggle()
        }
    }
}

