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

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.components.ReportDialog
import cz.lastaapps.cvutbus.components.sendReport
import cz.lastaapps.cvutbus.navigation.Dests

@Composable
fun Buttons(
    navController: NavController,
    showAbout: Boolean, onAboutClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val uriHandler = LocalUriHandler.current

        Button(
            onClick = { navController.navigate(Dests.Routes.privacyPolicy) },
            Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.settings_button_privacy_policy),
                textAlign = TextAlign.Center
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
        ) {
            AboutButton(
                showAbout = showAbout,
                onAboutClicked = onAboutClicked,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )

            Button(
                onClick = { uriHandler.openUri("https://play.google.com/store/apps/details?id=cz.lastaapps.cvutbus") },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) { Text(stringResource(R.string.settings_button_rate), textAlign = TextAlign.Center) }
        }

        ReportButton(Modifier.fillMaxWidth())
    }
}

@Composable
private fun ReportButton(modifier: Modifier = Modifier) {
    var shown by rememberSaveable { mutableStateOf(false) }

    Button(onClick = { shown = true }, modifier) {
        Text(stringResource(R.string.settings_button_report), textAlign = TextAlign.Center)
    }

    val context = LocalContext.current
    ReportDialog(shown, { shown = false }) {
        sendReport(context, it)
        shown = false
    }
}

@Composable
private fun AboutButton(
    showAbout: Boolean, onAboutClicked: () -> Unit, modifier: Modifier = Modifier
) {
    if (!showAbout) return

    Button(onClick = onAboutClicked, modifier) {
        Text(stringResource(R.string.settings_button_about))
    }
}
