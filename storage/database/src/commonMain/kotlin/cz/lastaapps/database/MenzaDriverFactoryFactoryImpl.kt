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

package cz.lastaapps.database

import com.squareup.sqldelight.db.SqlDriver
import pid.*

interface MenzaDriverFactory {
    fun createDriver(): SqlDriver
}

expect class MenzaDriverFactoryFactoryImpl : MenzaDriverFactory {
    override fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: MenzaDriverFactory): PIDDatabase {

    val driver = driverFactory.createDriver()
    val cl = ColumnConvertors

    return PIDDatabase(
        driver,
        calendarAdapter = Calendar.Adapter(
            service_idAdapter = cl.serviceId,
            daysAdapter = cl.serviceDays,
            start_dateAdapter = cl.localDate,
            end_dateAdapter = cl.localDate,
        ),
        routesAdapter = Routes.Adapter(
            route_idAdapter = cl.routeId,
        ),
        stopsAdapter = Stops.Adapter(
            stop_idAdapter = cl.stopId,
        ),
        stopTimesAdapter = StopTimes.Adapter(
            trip_idAdapter = cl.tripId,
            stop_idAdapter = cl.stopId,
            arrival_timeAdapter = cl.localTime,
            departure_timeAdapter = cl.localTime,
        ),
        tripsAdapter = Trips.Adapter(
            trip_idAdapter = cl.tripId,
            route_idAdapter = cl.routeId,
            service_idAdapter = cl.serviceId,
            directionAdapter = cl.direction,
        ),
    )
}