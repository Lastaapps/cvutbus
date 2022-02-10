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

package cz.lastaapps.cvutbus

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import cz.lastaapps.cvutbus.components.pid.PIDViewModel
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.ui.root.AppLayout
import dagger.hilt.android.AndroidEntryPoint
import org.lighthousegames.logging.logging

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    companion object {
        private val log = logging()
    }

    private val pidViewModel: PIDViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var composeReady = false

        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition {
            (!(pidViewModel.isReady.value && composeReady)).also {
                if (!it) log.i { "Dismissing splash screen" }
            }
        }

        setContent {
            AppLayout(this, this, pidViewModel, settingsViewModel) {
                composeReady = true
            }
        }
    }
}
