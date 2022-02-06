/*
 * Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 * This file is part of ČVUT Bus.
 *
 * Menza is free software: you can redistribute it and/or modify
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

package cz.lastaapps.cvutbus.ui.pid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import cz.lastaapps.cvutbus.api.PIDRepoProvider
import cz.lastaapps.cvutbus.getRoundedNow
import cz.lastaapps.cvutbus.secondTicker
import cz.lastaapps.cvutbus.ui.settings.SettingsStore
import cz.lastaapps.entity.utils.CET
import cz.lastaapps.repo.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class PIDViewModel @Inject constructor(
    private val provider: PIDRepoProvider,
    private val store: SettingsStore,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val directionSaveKey = "direction"
        private const val stopsPairSaveKey = "stopPair"
        private const val showCounterSaveKey = "showCounter"
        private const val showPastForSeconds = 60
    }

    val direction: StateFlow<Direction> get() = mDirection

    private val mDirection = MutableStateFlow(
        when (savedStateHandle.get<Boolean>(directionSaveKey)) {
            true -> Direction.Outbound
            false -> Direction.Inbound
            else -> Direction.Outbound
        }
    )

    fun setDirection(direction: Direction) {
        savedStateHandle[directionSaveKey] = direction == Direction.Outbound
        mDirection.tryEmit(direction)
    }


    val stops: StateFlow<StopPair> get() = mStops

    private val mStops = MutableStateFlow(
        savedStateHandle.get<Int>(stopsPairSaveKey)?.let {
            StopPairs.getPairById(it)
        } ?: StopPairs.strahovDejvicka
    )

    fun setStops(stops: StopPair) {
        savedStateHandle[stopsPairSaveKey] = stops.id
        mStops.tryEmit(stops)
    }

    val transportConnection: Flow<TransportConnection>
        get() = stops.combine(direction) { stops, directions ->
            TransportConnection(stops.stop1, stops.stop2, directions)
        }


    val showCounter: StateFlow<Boolean> get() = mShowCounter
    private val mShowCounter = MutableStateFlow(
        savedStateHandle.get<Boolean>(showCounterSaveKey) ?: false
    )

    fun setShowCounter(show: Boolean) {
        savedStateHandle[showCounterSaveKey] = show
        mShowCounter.tryEmit(show)
    }


    @OptIn(ObsoleteCoroutinesApi::class)
    fun getData(): Flow<List<DepartureInfo>> = channelFlow {
        val repo = provider.provide()
        transportConnection.collectLatest { connection ->

            var list = getRoundedNow().toLocalDateTime(CET).let { now ->
                repo.getData(now, connection).dropOld(now)
            }

            secondTicker { now ->
                val showLimit =
                    now.minus(showPastForSeconds, DateTimeUnit.SECOND).toLocalDateTime(CET)
                list = list.dropOld(showLimit)
                trySend(list)
            }
        }
    }

    private fun List<DepartureInfo>.dropOld(limit: LocalDateTime): List<DepartureInfo> {
        var list = this
        while (list.isNotEmpty()) {
            list = list.first().takeIf { it.dateTime <= limit }?.let { list.drop(1) } ?: break
        }
        return list
    }
}