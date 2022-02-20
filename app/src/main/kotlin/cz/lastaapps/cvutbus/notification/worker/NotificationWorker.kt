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

package cz.lastaapps.cvutbus.notification.worker

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import cz.lastaapps.cvutbus.components.settings.SettingsStore
import cz.lastaapps.cvutbus.components.settings.modules.notificationHide
import cz.lastaapps.cvutbus.minuteTickerStopAble
import cz.lastaapps.repo.DepartureInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging
import kotlin.time.Duration

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val state: WorkerState,
    private val store: SettingsStore,
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val notificationId = 42_221
        const val workerKey = "NotificationWorker"
        private val log = logging()
    }

    private val notificationManager = NotificationManagerCompat.from(appContext)
    private val notificationCreator = NotificationCreator(appContext, id)

    override suspend fun doWork(): Result {
        log.i { "Starting" }
        postLoadingNotification()

        val started = Clock.System.now()
        val timeToStop = store.notificationHide.first().takeIf { it != Duration.ZERO }
            ?.let { started.plus(it) }

        try {
            state.prepareData()

            val data = state.getData()
            coroutineScope {
                var job: Job? = null
                job = launch {
                    data.collectLatest {
                        minuteTickerStopAble { now ->
                            if (timeToStop != null && timeToStop < now) {
                                false
                            } else {
                                updateNotification(it)
                                true
                            }
                        }
                        showAutoHideNotification()
                        delay(10_000)

                        dismissNotification()
                        while (job == null) delay(1)
                        job!!.cancel()
                    }
                }
                job.join()
            }
        } catch (cancellation: CancellationException) {
            log.i { "Canceled" }
            dismissNotification()
        }

        log.i { "Ending" }
        dismissNotification()
        return Result.success()
    }

    private fun postLoadingNotification() {
        notificationManager.notify(
            notificationId,
            notificationCreator.createPlaceholderNotification()
        )
    }

    private fun updateNotification(data: List<DepartureInfo>) {
        notificationManager.notify(notificationId, notificationCreator.createTimeNotification(data))
    }

    private fun showAutoHideNotification() {
        notificationManager.notify(notificationId, notificationCreator.createAutoHideNotification())
    }

    private fun dismissNotification() {
        notificationManager.cancel(notificationId)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(notificationId, notificationCreator.createPlaceholderNotification())
    }
}