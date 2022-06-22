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

import cz.lastaapps.entity.StopName

object StopPairs {
    private var id = 0

    val allStops = listOf(
        StopPair(id++, StopName("Koleje Strahov"), StopName("Dejvická")),
        StopPair(id++, StopName("Stadion Strahov"), StopName("Karlovo náměstí")),
        StopPair(id++, StopName("Stadion Strahov"), StopName("Anděl")),
        StopPair(id++, StopName("Lotyšská"), StopName("Karlovo náměstí")),
        StopPair(id++, StopName("Újezd"), StopName("Petřín")),
    )

    fun getPairById(id: Int): StopPair? =
        allStops.firstOrNull { it.id == id }
}
