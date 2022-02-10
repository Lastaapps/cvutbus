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

package cz.lastaapps.repo

import cz.lastaapps.database.PIDDatabase
import cz.lastaapps.entity.hasDay
import cz.lastaapps.entity.utils.ServiceDayTime
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import kotlinx.datetime.*
import org.lighthousegames.logging.logging

class PIDRepoImpl(private val database: PIDDatabase) : PIDRepo {

    companion object {
        /**
         * To how many days each entry should be expanded
         * Required for service time -> normal time conversion (today + yesterday)
         * + 1 more day because of the after midnight times
         */
        private const val generateForDays = 3

        init {
            generateForDays shouldBeGreaterThanOrEqual 3
        }

        private val log = logging()
    }

    override suspend fun getData(
        fromDateTime: LocalDateTime, connection: TransportConnection,
    ): List<DepartureInfo> {

        log.i { "Loading data from $fromDateTime for connection $connection" }

        val maxStartDate = fromDateTime.date.plus(generateForDays, DateTimeUnit.DAY)
        val maxEndDate = fromDateTime.date.minus(1, DateTimeUnit.DAY)

        // get data for the both directions
        val rows = database.queriesQueries.getAllForDays(
            connection.from, connection.to, maxStartDate, maxEndDate,
        ).executeAsList()
        //is a little bit slower
        //val rows = database.queriesQueries.getAll(connection.from, connection.to).executeAsList()

        // we need to start 1 day before, service day can have up to 47 hours
        val startDate = fromDateTime.date.minus(1, DateTimeUnit.DAY)
        // caching, creating objects just once
        val dayShifts = List(generateForDays) { startDate.plus(it.toLong(), DateTimeUnit.DAY) }

        val times = mutableListOf<DepartureInfo>()
        rows.forEach { row ->
            // select the correct direction
            if (row.startArrivalTime.toDaySeconds() < row.endArrivalTime.toDaySeconds()) {
                dayShifts.forEach { date ->
                    // is time valid
                    if (row.startDate <= date && date <= row.endDate && row.days.hasDay(date)) {
                        times.add(
                            DepartureInfo(
                                serviceToNormalDateTime(date, row.startArrivalTime),
                                row.shortName, connection,
                            )
                        )
                    }
                }
            }
        }

        log.i { "Got ${times.size} results" }
        times.sort()
        val index = times.binarySearchBy(fromDateTime) { it.dateTime }

        val validTimes =
            // there is an item with the exact same time as the one requested
            if (index >= 0) {
                //there may be same departure time twice from two or more different trips
                var newIndex = index
                while (newIndex > 0 && times[newIndex - 1].dateTime == fromDateTime) {
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

    private fun serviceToNormalDateTime(date: LocalDate, time: ServiceDayTime): LocalDateTime {
        val plusDays = time.hours / 24
        val newDate = date.plus(plusDays, DateTimeUnit.DAY)
        val newHours = time.hours - plusDays * 24
        return LocalDateTime(
            newDate.year, newDate.month, newDate.dayOfMonth,
            newHours, time.minutes, time.seconds, 0
        )
    }
}

fun List<DepartureInfo>.dropOld(limit: LocalDateTime): List<DepartureInfo> {
    var list = this
    while (list.isNotEmpty()) {
        list = list.first().takeIf { it.dateTime <= limit }?.let { list.drop(1) } ?: break
    }
    return list
}
