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

package cz.lastaapps.database.util

import cz.lastaapps.core.domain.model.StopName
import cz.lastaapps.core.domain.model.StopPair
import cz.lastaapps.database.domain.model.RecordDto
import cz.lastaapps.database.domain.model.calendar.ServiceDayTime
import cz.lastaapps.database.domain.model.calendar.ServiceDays
import cz.lastaapps.database.domain.model.calendar.ServiceId
import cz.lastaapps.database.domain.model.route.RouteId
import cz.lastaapps.database.domain.model.stop.StopId
import cz.lastaapps.database.domain.model.trip.TripId
import kotlinx.datetime.LocalDate

internal val getAllColumnsMapper =
    { start_stop_id: StopId, start_stop_name: StopName, start_arrival_time: ServiceDayTime, start_departure_time: ServiceDayTime, end_stop_id: StopId, end_stop_name: StopName, end_arrival_time: ServiceDayTime, end_departure_time: ServiceDayTime, trip_id: TripId, trip_headsign: String, service_id: ServiceId, days: ServiceDays, start_date: LocalDate, end_date: LocalDate, route_id: RouteId, route_short_name: String, route_long_name: String ->
        RecordDto(
            RecordDto.StopInfo(
                start_stop_id, start_stop_name, start_arrival_time, start_departure_time,
            ),
            RecordDto.StopInfo(
                end_stop_id, end_stop_name, end_arrival_time, end_departure_time,
            ),
            trip_id, trip_headsign,
            service_id, days, start_date, end_date,
            route_id, route_short_name, route_long_name,
        )
    }

internal val getAllConnectionMapper = { id: Long, fromStation: StopName, toStation: StopName ->
    StopPair(id.toInt(), fromStation, toStation)
}
