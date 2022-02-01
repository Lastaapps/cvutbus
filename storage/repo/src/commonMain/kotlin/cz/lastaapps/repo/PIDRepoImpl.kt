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

package cz.lastaapps.repo

import cz.lastaapps.database.PIDDatabase
import cz.lastaapps.entity.hasDay
import cz.lastaapps.entity.utils.ServiceDayTime
import kotlinx.datetime.*

class PIDRepoImpl(private val database: PIDDatabase) : PIDRepo {

    companion object {
        /**
         * To how many days each entry should be expanded
         */
        private const val generateForDays = 2
    }

    override suspend fun getData(
        fromDateTime: LocalDateTime, connection: TransportConnection,
    ): List<DepartureInfo> {

        val rows = database.queriesQueries.getAllForDirection(
            connection.from, connection.to, connection.direction,
        ).executeAsList()

        val startDate = fromDateTime.date.minus(1, DateTimeUnit.DAY)
        //caching, creating objects just once
        val dayShifts = List(generateForDays) { startDate.plus(it.toLong(), DateTimeUnit.DAY) }

        val times = mutableListOf<DepartureInfo>()
        rows.forEach { row ->
            dayShifts.forEach { date ->
                if (row.start_date <= date && date <= row.end_date && row.days.hasDay(date)) {
                    times.add(
                        DepartureInfo(
                            serviceToNormalDateTime(date, row.arrival_time),
                            row.route_short_name, row.trip_headsign, connection,
                        )
                    )
                }
            }
        }

        times.sort()
        val index = times.binarySearchBy(fromDateTime) { it.dateTime }

        val validTimes = if (index >= 0) {
            //there may be same departure time twice from two or more different trips
            var newIndex = index
            while (newIndex > 0 && times[newIndex - 1].dateTime == fromDateTime) {
                newIndex--
            }
            times.subList(fromIndex = newIndex, toIndex = times.lastIndex)
        } else {
            val newIndex = -1 * (index + 1)
            if (newIndex <= times.lastIndex)
                times.subList(fromIndex = newIndex, toIndex = times.lastIndex)
            else emptyList()
        }

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