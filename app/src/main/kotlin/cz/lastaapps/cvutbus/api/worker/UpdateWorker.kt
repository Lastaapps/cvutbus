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

import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import cz.lastaapps.cvutbus.BuildConfig
import cz.lastaapps.cvutbus.api.DatabaseProvider
import cz.lastaapps.cvutbus.api.DatabaseStore
import cz.lastaapps.cvutbus.ui.SafeToast
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.lighthousegames.logging.logging

@HiltWorker
class UpdateWorker @AssistedInject constructor(
    @Assisted app: Context,
    @Assisted val params: WorkerParameters,
    val store: DatabaseStore,
    val databaseProvider: DatabaseProvider,
) : CoroutineWorker(app, params) {

    companion object {
        const val workerPeriodKey = "database_update_period"
        const val workerOneTimeKey = "database_update_one_time"
        private const val notificationId = 20_059

        val log = logging()

        /**
         * Shifts data validity retrieved from server
         */
        const val dataInvalidDaysBeforeInvalidity = 3

        const val EXTRA_USER_REQUESTED = "user_requested"
    }

    private val isRequestedByUser = params.inputData.getBoolean(EXTRA_USER_REQUESTED, false)
    private val alwaysDownload = false // for testing, where unit tests should be
    private val showNotifications = isRequestedByUser || alwaysDownload

    val client = HttpClient(CIO)
    private val manager = NotificationManagerCompat.from(applicationContext)
    private val notifications = UpdateNotifications(applicationContext)

    override suspend fun doWork(): Result {
        log.i { "Starting update process" }
        if (BuildConfig.DEBUG)
            withContext(Dispatchers.Main) {
                yield() // if looper() haven't been called yet, may help
                SafeToast.makeTextAndShow(
                    applicationContext,
                    "Update r: $isRequestedByUser",
                    Toast.LENGTH_LONG,
                )
            }

        if (!checkUpdateRequired() && !BuildConfig.DEBUG) {
            log.i { "No data update required" }
            dismissNotification()
            return Result.success()
        }

        try {
            updatingNotification()

            val info = fetchConfig()
            if (info == store.databaseInfo.first() && !isRequestedByUser) {
                log.e { "No new data available" }
                noNewDataNotification()
                return Result.success()
            }

            log.i { "Downloading database" }
            val databaseBytes = downloadDatabase()
            yield()

            log.i { "Saving to file" }
            val filename = "temp_pid_database.db"
            val file = applicationContext.getDatabasePath(filename)
            writeToFile(file, databaseBytes)
            yield()

            log.i { "Updating database" }
            updateDatabase(filename)
            store.setDatabaseInfo(info)
            store.setLastUpdated()
            deleteDatabase(filename)

            log.i { "Done" }
            doneNotification()
            return Result.success()
        } catch (e: Exception) {
            log.e(e) { "Update failed" }
            failedNotification()
            return if (isRequestedByUser) Result.failure() else Result.retry()
        }
    }

    private fun updatingNotification() {
        if (showNotifications)
            manager.notify(notificationId, notifications.createUpdatingNotification())
    }

    private fun doneNotification() {
        if (showNotifications)
            manager.notify(notificationId, notifications.createDoneNotification())
    }

    private fun noNewDataNotification() {
        if (showNotifications)
            manager.notify(notificationId, notifications.createNoNewAvailableNotification())
    }

    private fun failedNotification() {
        if (showNotifications)
            manager.notify(notificationId, notifications.createFailedNotification())
    }

    private fun dismissNotification() {
        manager.cancel(notificationId)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(notificationId, notifications.createUpdatingNotification())
    }
}
