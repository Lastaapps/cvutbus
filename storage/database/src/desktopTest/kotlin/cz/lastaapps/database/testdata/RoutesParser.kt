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

package cz.lastaapps.database.testdata

import cz.lastaapps.entity.Route
import cz.lastaapps.entity.RouteId
import java.io.BufferedReader
import java.io.InputStreamReader

object RoutesParser {

    fun parse(): List<Route> {
        val stream = ResourceOpener.openResource("routes")
        val buffered = BufferedReader(InputStreamReader(stream))

        buffered.readLine()
        return buffered.readLines().map {
            val (routeId, _, short, long) = it.safeSplit()
            Route(RouteId(routeId.drop(1).toInt()), short, long)
        }
    }
}