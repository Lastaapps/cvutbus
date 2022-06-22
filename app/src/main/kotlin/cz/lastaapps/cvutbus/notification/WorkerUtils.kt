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

package cz.lastaapps.cvutbus.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.asFlow
import androidx.work.*
import cz.lastaapps.cvutbus.notification.worker.NotificationWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.lighthousegames.logging.logging

@Suppress("MemberVisibilityCanBePrivate")
class WorkerUtils(private val context: Context) {

    companion object {
        private val log = logging()
    }

    private val manager = WorkManager.getInstance(context)

    fun start() {
        log.i { "Starting" }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            log.e { "Start aborted, notification permission not granted" }
            return
        }

        val work = with(OneTimeWorkRequestBuilder<NotificationWorker>()) {
            setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            build()
        }

        manager.enqueueUniqueWork(NotificationWorker.workerKey, ExistingWorkPolicy.KEEP, work)
    }

    fun cancel() {
        log.i { "Canceling" }
        manager.cancelUniqueWork(NotificationWorker.workerKey)
    }

    suspend fun toggle() {
        log.i { "Toggling" }
        if (!isRunning()) start() else cancel()
    }

    suspend fun restart() {
        log.i { "Restarting" }
        if (isRunning()) start()
    }

    suspend fun isRunning(): Boolean = isRunningFlow().first()

    fun isRunningFlow(): Flow<Boolean> {
        return manager.getWorkInfosForUniqueWorkLiveData(NotificationWorker.workerKey)
            .asFlow().map { it.firstOrNull()?.state == WorkInfo.State.RUNNING }
    }
}