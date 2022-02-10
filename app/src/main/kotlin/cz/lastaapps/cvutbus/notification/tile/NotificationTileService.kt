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

package cz.lastaapps.cvutbus.notification.tile

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import cz.lastaapps.cvutbus.notification.WorkerUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.lighthousegames.logging.logging

@RequiresApi(Build.VERSION_CODES.N)
class NotificationTileService : TileService() {

    companion object {
        private val log = logging()
    }

    val details by lazy { WorkerUtils(this@NotificationTileService) }

    private val scope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    override fun onClick() {
        super.onClick()

        log.i { "Tile clicked" }
        scope.launch {
            details.toggle()
        }
    }

    override fun onTileAdded() {
        super.onTileAdded()

        runBlocking {
            details.isRunningFlow().collectLatest {
                log.i { "Tile initializing state" }
                qsTile.state = if (it) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                qsTile.updateTile()
            }
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        log.i { "Started listening" }

        job?.cancel()
        job = scope.launch {
            details.isRunningFlow().collectLatest {
                log.i { "Tile updating state" }
                qsTile.state = if (it) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                qsTile.updateTile()
            }
        }
    }

    override fun onStopListening() {
        super.onStopListening()
        log.i { "Stopped listening" }

        job?.cancel()
        job = null
    }
}