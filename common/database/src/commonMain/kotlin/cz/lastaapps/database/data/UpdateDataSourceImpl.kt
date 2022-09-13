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

package cz.lastaapps.database.data

import cz.lastaapps.core.domain.model.StopName
import cz.lastaapps.core.domain.model.StopPair
import cz.lastaapps.database.PIDDatabase
import cz.lastaapps.database.domain.UpdateDataSource
import cz.lastaapps.database.domain.model.RecordDto
import cz.lastaapps.database.domain.model.calendar.ServiceDayTime
import cz.lastaapps.database.domain.model.calendar.ServiceDays
import cz.lastaapps.database.domain.model.calendar.ServiceId
import cz.lastaapps.database.domain.model.route.RouteId
import cz.lastaapps.database.domain.model.stop.StopId
import cz.lastaapps.database.domain.model.trip.TripId
import cz.lastaapps.database.util.getAllColumnsMapper
import cz.lastaapps.database.util.getAllConnectionMapper
import kotlinx.datetime.LocalDate

internal class UpdateDataSourceImpl(private val database: PIDDatabase) : UpdateDataSource {
    override fun inTransaction(block: () -> Unit) {
        database.transaction() { block() }
    }

    override fun updateFromAnother(src: UpdateDataSource) {
        database.transaction {
            database.logicQueries.deleteAll()

            src.getAllStopPairs().forEach { pair ->
                insertStopPair(pair)
            }
            insertRecords(src.getAllRecords())
        }
    }

    override fun getAllStopPairs(): List<StopPair> =
        database.connectionsQueries.getAllConnections(getAllConnectionMapper).executeAsList()

    override fun getAllRecords(): List<RecordDto> =
        database.logicQueries.getAllColumns(getAllColumnsMapper).executeAsList()

    override fun getAllRecords(pair: StopPair): List<RecordDto> =
        database.logicQueries.getAllColumnsForPair(pair.stop1, pair.stop2, getAllColumnsMapper)
            .executeAsList()

    override fun insertStopPair(pair: StopPair) {
        with(pair) {
            database.connectionsQueries.insertConnection(id.toLong(), stop1, stop2)
        }
    }

    override fun insertRecords(list: List<RecordDto>) {
        list.forEach { row ->
            listOf(row.stop1, row.stop2).forEach { stop ->
                database.stopsQueries.insert(stop.stopId, stop.stopName)
                database.stopTimesQueries.insert(
                    stop.stopId, row.trip_id, stop.arrivalTime, stop.departureTime,
                )
            }

            database.routesQueries.insert(
                row.route_id, row.route_short_name, row.route_long_name,
            )

            database.tripsQueries.insert(
                row.trip_id, row.route_id, row.service_id, row.trip_headsign,
            )

            database.calendarQueries.insert(
                row.service_id, row.days, row.start_date, row.end_date,
            )
        }
    }

    override fun insertCalendar(
        serviceId: ServiceId, days: ServiceDays, start: LocalDate, end: LocalDate
    ) = database.calendarQueries.insert(serviceId, days, start, end)

    override fun insertRoute(routeId: RouteId, shortName: String, longName: String) =
        database.routesQueries.insert(routeId, shortName, longName)

    override fun insertStop(stopId: StopId, stopName: StopName) =
        database.stopsQueries.insert(stopId, stopName)

    override fun insertStopTime(
        stopId: StopId, tripId: TripId, arrival: ServiceDayTime, departure: ServiceDayTime
    ) = database.stopTimesQueries.insert(stopId, tripId, arrival, departure)

    override fun insertTrip(
        tripId: TripId, routeId: RouteId, serviceId: ServiceId, headSign: String
    ) = database.tripsQueries.insert(tripId, routeId, serviceId, headSign)
}
