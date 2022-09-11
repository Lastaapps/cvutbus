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

package cz.lastaapps.database.domain.model

import cz.lastaapps.database.util.CET
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant

data class DepartureInfo(
    val dateTime: Instant,
    val routeShortName: String,
    val connection: TransportConnection,
) : Comparable<DepartureInfo> {

    /** Used for tests only */
    constructor(
        dateTime: LocalDateTime,
        routeShortName: String,
        connection: TransportConnection,
    ) : this(dateTime.toInstant(CET), routeShortName, connection)

    override fun compareTo(other: DepartureInfo): Int {
        return dateTime.compareTo(other.dateTime)
    }
}