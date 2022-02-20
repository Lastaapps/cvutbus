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

package cz.lastaapps.cvutbus.components.battery

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel

@Composable
fun BatteryChoiceDialog(viewModel: SettingsViewModel, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest) {
        BatteryChoice(viewModel, onDismissRequest, Modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryChoice(
    viewModel: SettingsViewModel,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.battery_text_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                stringResource(R.string.battery_text_explanation),
                textAlign = TextAlign.Center,
            )

            Row(
                Modifier.height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        openBatterySettings(context)
                        onAction()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Text(stringResource(R.string.battery_button_grant))
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setBatteryDismissed(true)
                        onAction()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LocalContentColor.current
                    ),
                    border = BorderStroke(1.dp, LocalContentColor.current),
                ) {
                    Text(stringResource(R.string.battery_button_dismiss))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryWaring(settingsViewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Card(
        modifier,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.battery_text_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                stringResource(R.string.battery_text_explanation),
                textAlign = TextAlign.Center,
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    openBatterySettings(context)
                    settingsViewModel.setBatteryDismissed(false)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            ) {
                Text(stringResource(R.string.battery_button_grant))
            }
        }
    }
}

private fun openBatterySettings(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        error("Cannot optimize battery on this version of Android")

    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
    context.startActivity(intent)
}
