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

import cz.lastaapps.entity.Stop
import cz.lastaapps.entity.StopId
import cz.lastaapps.entity.StopName
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object StopsParser {

    fun parse(stream: InputStream): List<Stop> {
        val buffered = BufferedReader(InputStreamReader(stream))

        return buffered.readLines().drop(1).map { line ->
            val (stopId, name) = line.safeSplit()
            Stop(StopId(stopId), StopName(name))
        }
    }

    fun parse(stream: InputStream, onEach: (Stop) -> Unit) {
        val buffered = BufferedReader(InputStreamReader(stream))

        buffered.readLine()
        return buffered.lines().forEach { line ->
            val (stopId, name) = line.safeSplit()
            Stop(StopId(stopId), StopName(name)).also(onEach)
        }
    }
}