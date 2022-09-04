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

import cz.lastaapps.database.domain.model.calendar.ServiceDayTime
import cz.lastaapps.database.domain.model.calendar.ServiceDays
import cz.lastaapps.database.domain.model.calendar.ServiceId
import cz.lastaapps.database.domain.model.route.RouteId
import cz.lastaapps.database.domain.model.stop.StopId
import cz.lastaapps.database.domain.model.stop.StopName
import cz.lastaapps.database.domain.model.trip.TripId
import kotlinx.datetime.LocalDate

data class RecordDto(
    val stop1: StopInfo,
    val stop2: StopInfo,

    val trip_id: TripId,
    val trip_headsign: String,

    val service_id: ServiceId,
    val days: ServiceDays,
    val start_date: LocalDate,
    val end_date: LocalDate,

    val route_id: RouteId,
    val route_short_name: String,
    val route_long_name: String
) {
    data class StopInfo(
        val stopId: StopId,
        val stopName: StopName,
        val arrivalTime: ServiceDayTime,
        val departureTime: ServiceDayTime,
    )
}