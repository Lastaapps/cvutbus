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
import cz.lastaapps.cvutbus.getRoundedNow
import cz.lastaapps.cvutbus.minuteTicker
import cz.lastaapps.entity.utils.CET
import cz.lastaapps.repo.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerState @Inject constructor(
    private val repoProvider: PIDRepoProvider,
    private val store: SettingsStore,
) {

    private var isReady = false
    suspend fun prepareData() {
        if (isReady) return

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
        }

        connection = MutableStateFlow(
            TransportConnection.fromStopPair(preferredStopPair.stopPair, direction)
        )

        isReady = true
    }

    private lateinit var repo: PIDRepo
    private lateinit var connection: MutableStateFlow<TransportConnection>

    fun reverseDirection() {
        connection.value = connection.value.reversed
    }

    fun setStopPair(stopPair: StopPair) {
        connection.value = TransportConnection.fromStopPair(stopPair, connection.value.direction)
    }

    fun getData(): Flow<List<DepartureInfo>> = channelFlow {
        connection.collectLatest { connection ->
            var data = repo.getData(getRoundedNow().toLocalDateTime(CET), connection)
            minuteTicker { now ->
                data = data.dropOld(now.toLocalDateTime(CET))
                trySend(data)
            }
        }
    }
}