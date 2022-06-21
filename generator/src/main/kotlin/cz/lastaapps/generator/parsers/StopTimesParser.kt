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

package cz.lastaapps.generator.parsers

import cz.lastaapps.entity.StopId
import cz.lastaapps.entity.StopTime
import cz.lastaapps.entity.TripId
import cz.lastaapps.entity.utils.ServiceDayTime
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object StopTimesParser {

    fun parse(stream: InputStream): List<StopTime> {
        val buffered = BufferedReader(InputStreamReader(stream))

        buffered.readLine()
        return buffered.readLines().map { line ->
            val (tripId, arrival, departure, stopId) = line.safeSplit()
            StopTime(
                StopId(stopId),
                TripId(tripId),
                arrival.toServiceDayTime(),
                departure.toServiceDayTime()
            )
        }
    }

    fun parse(stream: InputStream, onEach: (StopTime) -> Unit) {
        val buffered = BufferedReader(InputStreamReader(stream))

        buffered.readLine()
        buffered.lines().forEach { line ->
            val (tripId, arrival, departure, stopId) = line.safeSplit()
            StopTime(
                StopId(stopId),
                TripId(tripId),
                arrival.toServiceDayTime(),
                departure.toServiceDayTime()
            ).also(onEach)
        }
    }

    private val patter = "(\\d{1,2}):(\\d{1,2}):(\\d{1,2})".toRegex()
    private fun String.toServiceDayTime(): ServiceDayTime {
        val (hour, minute, second) = patter.find(this)!!.destructured
        return ServiceDayTime.of(hour.toInt(), minute.toInt(), second.toInt())
    }
}