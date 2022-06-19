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

package cz.lastaapps.cvutbus.api.worker

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.asFlow
import androidx.work.*
import cz.lastaapps.cvutbus.BuildConfig
import cz.lastaapps.cvutbus.ui.SafeToast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.lighthousegames.logging.logging
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("MemberVisibilityCanBePrivate")
class UpdateManager @Inject constructor(val app: Application) {

    companion object {
        private val log = logging()
    }

    private val manager = WorkManager.getInstance(app)

    fun startNow() {
        log.i { "Starting" }
        if (BuildConfig.DEBUG)
            Toast.makeText(app, "Starting update worker", Toast.LENGTH_LONG).show()

        val data = with(Data.Builder()) {
            putBoolean(UpdateWorker.EXTRA_USER_REQUESTED, true)
            build()
        }

        val work = with(OneTimeWorkRequestBuilder<UpdateWorker>()) {
            setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            setInputData(data)
            build()
        }

        manager.enqueueUniqueWork(UpdateWorker.workerOneTimeKey, ExistingWorkPolicy.KEEP, work)
    }

    suspend fun schedule() {
        log.i { "Scheduling" }
        if (BuildConfig.DEBUG)
            SafeToast.makeTextAndShow(app, "Scheduling update worker", Toast.LENGTH_LONG)

        val constrains = with(Constraints.Builder()) {
            setRequiredNetworkType(NetworkType.CONNECTED)
            build()
        }
        val work = with(PeriodicWorkRequestBuilder<UpdateWorker>(Duration.ofDays(1))) {
            setConstraints(constrains)
            setBackoffCriteria(BackoffPolicy.LINEAR, 8, TimeUnit.HOURS)
            build()
        }

        manager.enqueueUniquePeriodicWork(
            UpdateWorker.workerPeriodKey, ExistingPeriodicWorkPolicy.REPLACE, work
        )
    }

    fun cancel() {
        log.i { "Canceling" }
        manager.cancelUniqueWork(UpdateWorker.workerOneTimeKey)
    }

    suspend fun isRunning(): Boolean = isRunningFlow().first()

    fun isRunningFlow(): Flow<Boolean> {

        val oneTime = manager.getWorkInfosForUniqueWorkLiveData(UpdateWorker.workerOneTimeKey)
            .asFlow().map { list ->
                list.firstOrNull()?.state == WorkInfo.State.RUNNING
            }

        val periodic = manager.getWorkInfosForUniqueWorkLiveData(UpdateWorker.workerPeriodKey)
            .asFlow().map { list ->
                list.firstOrNull()?.state == WorkInfo.State.RUNNING
            }

        return oneTime.combine(periodic) { first, second ->
            first || second
        }
    }
}