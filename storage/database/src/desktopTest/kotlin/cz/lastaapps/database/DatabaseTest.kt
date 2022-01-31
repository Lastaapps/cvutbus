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

import cz.lastaapps.database.testdata.*
import cz.lastaapps.entity.Direction
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalCoroutinesApi::class)
object DatabaseTest {

    lateinit var database: PIDDatabase
    const val strahov = "Koleje Strahov"
    const val dejvicka = "Dejvická"

    @OptIn(ExperimentalTime::class)
    @BeforeAll
    @JvmStatic
    fun initDatabase() {
        database = createDatabase(MemoryDriverFactory())
        measureTime {
            loadData(database)
        }.also {
            println("Database ready, took: $it")
        }
    }

    @Test
    fun stopsTest() = runTest {
        database.stopsQueries.getAllStops().executeAsList().shouldHaveSize(15594)
        database.stopsQueries.getStopsByName(listOf(strahov)).executeAsList().shouldHaveSize(2)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun filterTripNumber() = runTest {
        measureTime {
            database.queriesQueries.getTripIds(strahov, dejvicka).executeAsList()
                .shouldHaveSize(358)
        }.also { println("One query duration: $it") }
        database.queriesQueries.getTripIds(dejvicka, strahov).executeAsList().shouldHaveSize(358)
    }

    @Test
    fun directions() = runTest {
        database.queriesQueries.getAllForDirection(strahov, dejvicka, Direction.Inbound)
            .executeAsList().shouldHaveSize(176)
        database.queriesQueries.getAllForDirection(strahov, dejvicka, Direction.Outbound)
            .executeAsList().shouldHaveSize(182)
        database.queriesQueries.getAllForDirection(dejvicka, strahov, Direction.Inbound)
            .executeAsList().shouldHaveSize(176)
        database.queriesQueries.getAllForDirection(dejvicka, strahov, Direction.Outbound)
            .executeAsList().shouldHaveSize(182)
    }
}

private fun loadData(database: PIDDatabase) {
    database.transaction {
        CalendarParser.parse().forEach {
            database.calendarQueries.insert(it.serviceId, it.days, it.start, it.end)
        }
        RoutesParser.parse().forEach {
            database.routesQueries.insert(it.routeId, it.shortName, it.longName)
        }
        StopsParser.parse().forEach {
            database.stopsQueries.insert(it.stopId, it.name)
        }
        StopTimesParser.parse().forEach {
            database.stopTimesQueries.insert(it.stopId, it.tripId, it.arrival, it.departure)
        }
        TripsParser.parse().forEach {
            database.tripsQueries.insert(
                it.tripId,
                it.routeId,
                it.serviceId,
                it.headSign,
                it.direction
            )
        }
    }
}

