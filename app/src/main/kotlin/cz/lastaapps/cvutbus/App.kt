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

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import cz.lastaapps.cvutbus.api.DatabaseProvider
import cz.lastaapps.cvutbus.api.DatabaseStore
import cz.lastaapps.cvutbus.api.PIDRepoProvider
import cz.lastaapps.cvutbus.api.worker.UpdateManager
import cz.lastaapps.cvutbus.components.pid.PIDViewModel
import cz.lastaapps.cvutbus.components.privacy.PrivacyStore
import cz.lastaapps.cvutbus.components.privacy.PrivacyViewModel
import cz.lastaapps.cvutbus.components.settings.SettingsStore
import cz.lastaapps.cvutbus.components.settings.SettingsViewModel
import cz.lastaapps.cvutbus.init.RunInit
import cz.lastaapps.cvutbus.notification.receivers.RegisterModule
import cz.lastaapps.cvutbus.notification.worker.WorkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.kodein.di.*
import org.lighthousegames.logging.logging

class App : Application(), Configuration.Provider, DIAware {

    @Suppress("RemoveExplicitTypeArguments")
    override val di: DI by DI.lazy {
        bindProvider<Context> { this@App }
        bindProvider<App> { this@App }

        bindSingleton<WorkerState> { WorkerState(instance(), instance()) }

        bindSingleton<DatabaseStore> { DatabaseStore(instance()) }
        bindSingleton<DatabaseProvider> { DatabaseProvider(instance(), instance(), Dispatchers.IO) }
        bindSingleton<PIDRepoProvider> { PIDRepoProvider(instance()) }
        bindSingleton<UpdateManager> { UpdateManager(instance()) }
        bindProvider<RegisterModule> { RegisterModule(instance(), instance()) }
        bindProvider<RunInit> { RunInit(instance(), instance(), instance()) }

        bindSingleton<SettingsStore> { SettingsStore(instance()) }
        bindSingleton<PrivacyStore> { PrivacyStore(instance()) }

        bindProvider<PIDViewModel> { PIDViewModel(instance(), instance()) }
        bindProvider<PrivacyViewModel> { PrivacyViewModel(instance()) }
        bindProvider<SettingsViewModel> {
            SettingsViewModel(
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
    }

    companion object {
        private val log = logging()
        val appScope = CoroutineScope(Dispatchers.Main)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder().build()


    override fun onCreate() {
        super.onCreate()
        log.i { "Creating App" }
    }
}