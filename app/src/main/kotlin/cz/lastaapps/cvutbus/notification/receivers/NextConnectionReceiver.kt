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

package cz.lastaapps.cvutbus.notification.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cz.lastaapps.cvutbus.notification.WorkerUtils
import cz.lastaapps.cvutbus.notification.worker.WorkerState
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.lighthousegames.logging.logging

class NextConnectionReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        private val log = logging()
    }

    override fun onReceive(context: Context, intent: Intent) {
        log.i { "Changing direction" }
        runBlocking {
            WorkerUtils(context).start()
            val state = get<WorkerState>()
            state.nextStopPair()
        }
    }
}