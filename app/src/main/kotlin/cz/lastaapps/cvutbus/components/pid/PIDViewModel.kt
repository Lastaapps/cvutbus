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

package cz.lastaapps.cvutbus.components.pid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import cz.lastaapps.cvutbus.api.PIDRepoProvider
import cz.lastaapps.cvutbus.components.settings.SettingsStore
import cz.lastaapps.cvutbus.components.settings.modules.*
import cz.lastaapps.cvutbus.getRoundedNow
import cz.lastaapps.cvutbus.secondTicker
import cz.lastaapps.database.domain.model.DepartureInfo
import cz.lastaapps.database.domain.model.Direction
import cz.lastaapps.database.domain.model.TransportConnection
import cz.lastaapps.database.util.CET
import cz.lastaapps.repo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.lighthousegames.logging.logging

@OptIn(ExperimentalPagerApi::class)
class PIDViewModel constructor(
    private val provider: PIDRepoProvider,
    private val store: SettingsStore,
) : ViewModel() {

    companion object {
        private const val showPastForSeconds = 60
        private val log = logging()
    }

    val isReady: StateFlow<Boolean>
        get() = mIsReady

    private val mIsReady = MutableStateFlow(false)

    val direction: StateFlow<Direction> get() = mDirection

    private lateinit var mDirection: MutableStateFlow<Direction>

    fun setDirection(direction: Direction) {
        mDirection.tryEmit(direction)
        viewModelScope.launch { store.setLatestDirection(direction) }
    }

    fun onPage(page: Int) {
        viewModelScope.launch { store.setLatestStopPair(getPairForPage(page)) }
    }

    val showCounter: StateFlow<Boolean> get() = mShowCounter
    private lateinit var mShowCounter: MutableStateFlow<Boolean>

    fun setShowCounter(showCounter: Boolean) {
        mShowCounter.tryEmit(showCounter)
        viewModelScope.launch { store.setWasCounter(showCounter) }
    }

    init {
        viewModelScope.launch(Dispatchers.Default) {
            log.i { "Initializing" }

            val preferredDirection = store.preferredDirection.first()
            val latestDirection = store.latestDirection.first()
            val preferredStopPair = store.preferredStopPair.first()
            val showMode = store.timeShowMode.first()
            val latestWasCounter = store.latestWasCounter.first()

            mDirection = MutableStateFlow(
                when (preferredDirection) {
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
            )

            mShowCounter = MutableStateFlow(
                when (showMode) {
                    TimeShowMode.Countdown -> true
                    TimeShowMode.Remember -> latestWasCounter
                    TimeShowMode.Time -> false
                }
            )

            pagerState = PagerState(
                StopPairs.allStops.indexOf(preferredStopPair.stopPair).takeIf { it >= 0 } ?: 0
            )

            log.i { "Initialization done" }
            mIsReady.value = true
        }
    }

    lateinit var pagerState: PagerState
    val pageCount = StopPairs.allStops.size
    private fun getPairForPage(page: Int) = StopPairs.allStops[page]

    fun getData(page: Int): Flow<List<DepartureInfo>> = channelFlow {
        val pair = getPairForPage(page)
        val repo = provider.provide()
        val start = getRoundedNow().toLocalDateTime(CET)
        direction.collectLatest { dir ->
            repo.getData(start, TransportConnection.fromStopPair(pair, dir))
                .map { it.dropOld(start) }
                .collectLatest { dbList ->
                    var list = dbList
                    secondTicker { now ->
                        val showLimit =
                            now.minus(showPastForSeconds, DateTimeUnit.SECOND).toLocalDateTime(CET)
                        list = list.dropOld(showLimit)
                        trySend(list)
                    }
                }
        }
    }

    fun getTransportConnection(page: Int): Flow<TransportConnection> =
        direction.map { TransportConnection.fromStopPair(getPairForPage(page), it) }
}