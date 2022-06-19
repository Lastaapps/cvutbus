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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.annotation.RequiresApi
import cz.lastaapps.cvutbus.MainActivity
import cz.lastaapps.cvutbus.R
import cz.lastaapps.cvutbus.notification.WorkerUtils
import cz.lastaapps.cvutbus.ui.SafeToast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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
            val context = this@NotificationTileService

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ) {
                details.toggle()
            } else {
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivityAndCollapse(intent)
                SafeToast.makeTextAndShow(
                    context,
                    R.string.notification_tile_permission_missing,
                    Toast.LENGTH_LONG
                )
            }
        }
    }

    override fun onTileAdded() {
        super.onTileAdded()

        runBlocking(Dispatchers.Default) {
            log.i { "Tile initializing state" }
            val state = details.isRunningFlow().first()
            qsTile.state = if (state) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            qsTile.updateTile()
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        log.i { "Started listening" }

        job?.cancel()
        job = scope.launch(Dispatchers.Default) {
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

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}