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

package cz.lastaapps.generator.model

import cz.lastaapps.database.domain.model.StopPair
import cz.lastaapps.database.domain.model.stop.StopName

object StopPairs {
    val allStops = listOf(
        StopPair(0, StopName("Koleje Strahov"), StopName("Dejvická")),
        StopPair(1, StopName("Stadion Strahov"), StopName("Karlovo náměstí")),
        StopPair(2, StopName("Stadion Strahov"), StopName("Anděl")),
        StopPair(3, StopName("Lotyšská"), StopName("Karlovo náměstí")),
        StopPair(4, StopName("Újezd"), StopName("Petřín")),
    )
}
