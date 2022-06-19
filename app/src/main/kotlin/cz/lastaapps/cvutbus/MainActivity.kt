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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import cz.lastaapps.cvutbus.components.pid.PIDViewModel
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.init.RunInit
import cz.lastaapps.cvutbus.ui.root.AppLayout
import kotlinx.coroutines.delay
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.android.x.viewmodel.viewModel
import org.kodein.di.instance
import org.lighthousegames.logging.logging

class MainActivity : AppCompatActivity(), DIAware {

    companion object {
        private val log = logging()
    }

    override val di: DI by closestDI()

    private val init: RunInit by instance()

    private val pidViewModel: PIDViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var composeReady = false

        lifecycleScope.launchWhenStarted {
            delay(5000)
            init.checkFirstLaunch()
        }

        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition {
            (!(pidViewModel.isReady.value && composeReady)).also {
                if (!it) log.i { "Dismissing splash screen" }
            }
        }

        supportActionBar?.hide()

        setContent {
            AppLayout(this, this, pidViewModel, settingsViewModel) {
                composeReady = true
            }
        }
    }
}
