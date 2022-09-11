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

package cz.lastaapps.database.data

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import cz.lastaapps.database.PIDDatabase
import cz.lastaapps.database.domain.PIDDataSource
import cz.lastaapps.database.domain.model.DepartureInfo
import cz.lastaapps.database.domain.model.StopPair
import cz.lastaapps.database.domain.model.TransportConnection
import cz.lastaapps.database.domain.model.calendar.hasDay
import cz.lastaapps.database.util.CET
import cz.lastaapps.database.util.getAllConnectionMapper
import cz.lastaapps.database.util.serviceToNormalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*
import org.koin.core.annotation.Factory
import org.lighthousegames.logging.logging
import pid.GetAllForDays

@Factory
internal class PIDDataSourceImpl(
    private val database: PIDDatabase,
) : PIDDataSource {

    companion object {
        private val log = logging()

        /**
         * To how many days each entry should be expanded
         * Required for service time -> normal time conversion (today + yesterday)
         * + 1 more day because of the after midnight times
         */
        private const val generateForDays = 3

        init {
            check(generateForDays >= 3)
        }
    }


    override suspend fun getConnections(): Flow<List<StopPair>> =
        database.connectionsQueries.getAllConnections(getAllConnectionMapper).asFlow().mapToList()

    override suspend fun getData(
        from: Instant, connection: TransportConnection,
    ): Flow<List<DepartureInfo>> {

        // to prevent summer/winter time changes
        val fromDateTime = from.toLocalDateTime(CET)
        log.i { "Loading data from $fromDateTime CET for connection $connection" }

        val maxStartDate = fromDateTime.date.plus(generateForDays, DateTimeUnit.DAY)
        val maxEndDate = fromDateTime.date.minus(1, DateTimeUnit.DAY)

        //is a little bit slower
        //val rows = database.logicQueries.getAll(connection.from, connection.to)

        // get data for the both directions
        val rows = database.logicQueries.getAllForDays(
            connection.from, connection.to, maxStartDate, maxEndDate,
        )

        return rows.asFlow().mapToList()
            .map { it.getForRows(fromDateTime.toInstant(CET), connection) }
    }

    private fun List<GetAllForDays>.getForRows(
        from: Instant,
        connection: TransportConnection
    ): List<DepartureInfo> {
        // we need to start 1 day before, service day can have up to 47 hours
        val fromDateTime = from.toLocalDateTime(CET)
        val startDate = fromDateTime.date.minus(1, DateTimeUnit.DAY)
        // caching, creating objects just once
        val dayShifts = List(generateForDays) { startDate.plus(it.toLong(), DateTimeUnit.DAY) }

        val times = mutableListOf<DepartureInfo>()
        this.forEach { row ->
            // select the correct direction
            if (row.startArrivalTime < row.endArrivalTime) {
                dayShifts.forEach { date ->
                    // is time valid
                    if (row.startDate <= date && date <= row.endDate && row.days.hasDay(date)) {
                        times.add(
                            DepartureInfo(
                                serviceToNormalDateTime(date, row.startArrivalTime).toInstant(CET),
                                row.shortName, connection,
                            )
                        )
                    }
                }
            }
        }

        log.i { "Got ${times.size} results" }
        times.sort()
        val index = times.binarySearchBy(from) { it.dateTime }

        val validTimes =
            // there is an item with the exact same time as the one requested
            if (index >= 0) {
                //there may be same departure time twice from two or more different trips
                var newIndex = index
                while (newIndex > 0 && times[newIndex - 1].dateTime == from) {
                    newIndex--
                }
                times.subList(fromIndex = newIndex, toIndex = times.lastIndex)
            } else {
                // there is no item, we need to select the next one
                val newIndex = -1 * (index + 1)
                if (newIndex <= times.lastIndex)
                    times.subList(fromIndex = newIndex, toIndex = times.lastIndex)
                // there are no data left, user should probably update data
                else emptyList()
            }

        log.i { "Filtered to ${validTimes.size} results" }
        return validTimes
    }
}