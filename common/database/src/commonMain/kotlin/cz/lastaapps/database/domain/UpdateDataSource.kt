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

package cz.lastaapps.database.domain

import cz.lastaapps.database.domain.model.RecordDto
import cz.lastaapps.database.domain.model.StopPair
import cz.lastaapps.database.domain.model.calendar.ServiceDayTime
import cz.lastaapps.database.domain.model.calendar.ServiceDays
import cz.lastaapps.database.domain.model.calendar.ServiceId
import cz.lastaapps.database.domain.model.route.RouteId
import cz.lastaapps.database.domain.model.stop.StopId
import cz.lastaapps.database.domain.model.stop.StopName
import cz.lastaapps.database.domain.model.trip.TripId
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface UpdateDataSource {

    fun inTransaction(block: () -> Unit)

    fun getAllRecords(pair: StopPair): Flow<List<RecordDto>>

    fun insertRecords(pair: StopPair, list: List<RecordDto>)

    fun insertCalendar(serviceId: ServiceId, days: ServiceDays, start: LocalDate, end: LocalDate)
    fun insertRoute(routeId: RouteId, shortName: String, longName: String)
    fun insertStop(stopId: StopId, stopName: StopName)
    fun insertStopTime(
        stopId: StopId,
        tripId: TripId,
        arrival: ServiceDayTime,
        departure: ServiceDayTime
    )

    fun insertTrip(tripId: TripId, routeId: RouteId, serviceId: ServiceId, headSign: String)
}
