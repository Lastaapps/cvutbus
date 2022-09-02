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
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.lighthousegames.logging.logging

//import org.koin.ksp.generated.*

class App : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        log.i { "Creating App" }

        startKoin {
            androidLogger()
            androidContext(this@App)
            workManagerFactory()
            modules(appModule)
//            defaultModule()
        }
    }

    private val appModule = module {
        singleOf(::WorkerState)

        single { DatabaseProvider(get(), get(), Dispatchers.IO) }
        singleOf(::DatabaseStore)
        singleOf(::PIDRepoProvider)
        singleOf(::UpdateManager)
        factoryOf(::RegisterModule)
        factoryOf(::RunInit)

        singleOf(::SettingsStore)
        singleOf(::PrivacyStore)

        viewModelOf(::PIDViewModel)
        viewModelOf(::PrivacyViewModel)
        viewModelOf(::SettingsViewModel)
    }

    companion object {
        private val log = logging()
        val appScope = CoroutineScope(Dispatchers.Main)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder().build()
}