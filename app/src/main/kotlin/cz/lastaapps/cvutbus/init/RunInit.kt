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

package cz.lastaapps.cvutbus.init

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import cz.lastaapps.cvutbus.App
import cz.lastaapps.cvutbus.api.worker.UpdateManager
import cz.lastaapps.cvutbus.notification.receivers.RegisterModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import javax.inject.Inject

class RunInit @Inject constructor(
    private val app: Application,
    private val registerModule: RegisterModule,
    private val updateManager: UpdateManager,
) {
    companion object {
        private val log = logging()

        private const val sharedPrefName = "is_first_run"
        private const val hasRunKey = "has_run"
    }

    fun run(delay: Long = 5000) {
        App.appScope.launch {
            delay(delay)
            log.i { "Running basic initialization" }
            registerModule.update()
            updateManager.schedule()
        }
    }

    suspend fun checkFirstLaunch() = withContext(Dispatchers.IO) {
        log.i { "Checking first run" }
        val pref = app.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        if (pref.getBoolean(hasRunKey, false)) return@withContext
        run()
        pref.edit {
            putBoolean(hasRunKey, true)
        }
    }
}