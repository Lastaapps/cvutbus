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

package cz.lastaapps.generator

import cz.lastaapps.database.MemoryDriverFactory
import cz.lastaapps.database.PIDDatabase
import cz.lastaapps.database.createDatabase
import cz.lastaapps.entity.StopName
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalCoroutinesApi::class)
class DatabaseTest {

    companion object {

        lateinit var database: PIDDatabase
        val strahov = StopName("Koleje Strahov")
        val dejvicka = StopName("Dejvická")

        @OptIn(ExperimentalTime::class)
        @BeforeAll
        @JvmStatic
        fun initDatabase() {
            database = createDatabase(MemoryDriverFactory())
            measureTime {
                loadData(File("src/desktopTest/kotlin/cz/lastaapps/generator/testdata"), database)
            }.also {
                println("Database ready, took: $it")
            }
        }
    }

    @Test
    fun stopsTest() = runTest {
        database.stopsQueries.getAll().executeAsList().shouldHaveSize(15594)
        database.stopsQueries.getStopsByName(listOf(strahov)).executeAsList().shouldHaveSize(2)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun filterTripNumber() = runTest {
        database.queriesQueries.getTripIds(strahov, dejvicka).executeAsList().shouldHaveSize(358)
        database.queriesQueries.getTripIds(dejvicka, strahov).executeAsList().shouldHaveSize(358)
    }

    @Test
    fun directions() = runTest {
        database.queriesQueries.getAll(strahov, dejvicka).executeAsList().shouldHaveSize(358)
        database.queriesQueries.getAll(dejvicka, strahov).executeAsList().shouldHaveSize(358)
    }
}
