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

import cz.lastaapps.cvutbus.api.PIDRepoProvider
import cz.lastaapps.cvutbus.components.settings.SettingsStore
import cz.lastaapps.cvutbus.components.settings.modules.PreferredDirection
import cz.lastaapps.cvutbus.components.settings.modules.latestDirection
import cz.lastaapps.cvutbus.components.settings.modules.preferredDirection
import cz.lastaapps.cvutbus.components.settings.modules.preferredStopPair
import cz.lastaapps.database.domain.model.*
import cz.lastaapps.database.util.CET
import cz.lastaapps.repo.*
import cz.lastaapps.repo.domain.usecases.PIDRepo
import cz.lastaapps.repo.util.getRoundedNow
import cz.lastaapps.repo.util.secondsTicker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.lighthousegames.logging.logging

class WorkerState constructor(
    private val repoProvider: PIDRepoProvider,
    private val store: SettingsStore,
) {
    companion object {
        private val log = logging()
    }

    private var isReady = false
    private val mutex = Mutex()

    suspend fun prepareData() = mutex.withLock {
        if (isReady) return

        log.i { "Initializing" }

        repo = repoProvider.provide()

        val preferredDirection = store.preferredDirection.first()
        val latestDirection = store.latestDirection.first()
        val preferredStopPair = store.preferredStopPair.first()

        val direction = when (preferredDirection) {
            PreferredDirection.Inbound -> Direction.Inbound
            PreferredDirection.Outbound -> Direction.Outbound
            PreferredDirection.Remember -> latestDirection
            PreferredDirection.TimeBased ->
                if (Clock.System.now().toLocalDateTime(CET).hour < 12)
                    Direction.Outbound else Direction.Inbound
            PreferredDirection.TimeBasedReversed ->
                if (Clock.System.now().toLocalDateTime(CET).hour >= 12)
                    Direction.Outbound else Direction.Inbound
        }

        connection = MutableStateFlow(
            TransportConnection.fromStopPair(preferredStopPair.stopPair, direction)
        )

        log.i { "Ready" }
        isReady = true
    }

    private lateinit var repo: PIDRepo
    private lateinit var connection: MutableStateFlow<TransportConnection>

    suspend fun reverseDirection() {
        prepareData()
        connection.value = connection.value.reversed
    }

    suspend fun nextStopPair() {
        val conn = connection.value.toStopPair(Int.MAX_VALUE)
        val stops = StopPairs.allStops
        val newIndex = stops.indexOfFirst { it.stop1 == conn.stop1 && it.stop2 == it.stop2 }
            .takeIf { it >= 0 }?.plus(1)?.mod(stops.size) ?: 0
        setStopPair(stops[newIndex])
    }

    private suspend fun setStopPair(stopPair: StopPair) {
        prepareData()
        connection.value = TransportConnection.fromStopPair(stopPair, connection.value.direction)
    }

    fun getData(): Flow<List<DepartureInfo>> = channelFlow {
        prepareData()
        connection.collectLatest { connection ->
            repo.getData(getRoundedNow().toLocalDateTime(CET), connection).collectLatest { dbData ->
                var data = dbData

                secondsTicker { now ->
                    data = data.dropOld(now.minus(30, DateTimeUnit.SECOND).toLocalDateTime(CET))
                    trySend(data)
                }
            }
        }
    }.distinctUntilChanged()
}