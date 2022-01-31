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

package cz.lastaapps.generator.parsers

import cz.lastaapps.entity.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object TripsParser {

    fun parse(stream: InputStream): List<Trip> {
        val buffered = BufferedReader(InputStreamReader(stream))

        return buffered.readLines().drop(1).map { line ->
            val split = line.safeSplit()
            Trip(
                RouteId.fromString(split[0]),
                ServiceId(split[1]),
                TripId(split[2]),
                split[3],
                Direction.withId(split[5].toInt()),
            )
        }
    }
}