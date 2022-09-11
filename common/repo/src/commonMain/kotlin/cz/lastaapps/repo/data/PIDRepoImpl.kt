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

package cz.lastaapps.repo.data

import cz.lastaapps.database.domain.PIDDataSource
import cz.lastaapps.database.domain.model.DepartureInfo
import cz.lastaapps.database.domain.model.TransportConnection
import cz.lastaapps.repo.domain.PIDRepo
import cz.lastaapps.repo.util.secondsTicker
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import org.lighthousegames.logging.logging
import kotlin.time.Duration

internal class PIDRepoImpl(private val pidDataSource: PIDDataSource) : PIDRepo {

    companion object {
        private val log = logging()
    }

    override suspend fun getData(
        from: Instant,
        connection: TransportConnection
    ): Flow<List<DepartureInfo>> {
        return pidDataSource.getData(from, connection)
    }

    override suspend fun getLatestData(
        connection: TransportConnection,
        nowProvider: () -> Instant,
        includePast: Duration
    ): Flow<List<DepartureInfo>> = channelFlow {
        getData(nowProvider() - includePast, connection).map { it.toImmutableList() }
            .collectLatest { source ->
                var list = source
                secondsTicker(getNow = nowProvider) { now ->
                    list.indexOfFirst { item ->
                        item.dateTime < now - includePast
                    }.takeIf { it != -1 && list.isNotEmpty() }?.let {
                        list = list.subList(it, list.size)
                        trySend(list)
                    }
                }
            }
    }
}
