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

package cz.lastaapps.repo

import cz.lastaapps.database.DatabaseDriverFactoryImpl
import cz.lastaapps.database.PIDDatabase
import cz.lastaapps.database.createDatabase
import io.kotest.matchers.collections.shouldContainInOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Month

@OptIn(ExperimentalCoroutinesApi::class)
class PIDRepoTest {

    companion object {
        lateinit var database: PIDDatabase

        @BeforeAll
        @JvmStatic
        fun initDatabase() {
            //accesses data generated on the 1st February 2022
            database =
                createDatabase(DatabaseDriverFactoryImpl(File("src/desktopTest/piddatabase.db")))
        }
    }

    @Test
    fun weekDayStrahovDejvicka() = runTest {
        val repo: PIDRepo = PIDRepoImpl(database)

        val date = LocalDate(2022, Month.FEBRUARY, 1)
        val dateTime = LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 0, 0)

        val toDejvickaConnection = TransportConnections.strahovDejvicka(true)
        val toStrahovConnection = TransportConnections.strahovDejvicka(false)

        val toDejvicka = repo.getData(dateTime, toDejvickaConnection).take(5)
        val toStrahov = repo.getData(dateTime, toStrahovConnection).take(5)

        toDejvicka shouldContainInOrder listOf(
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 1, 0),
                "143", toDejvickaConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 2, 0),
                "149", toDejvickaConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 13, 0),
                "143", toDejvickaConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 22, 0),
                "149", toDejvickaConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 28, 0),
                "143", toDejvickaConnection,
            ),
        )

        toStrahov shouldContainInOrder listOf(
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 11, 0),
                "143", toStrahovConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 12, 0),
                "149", toStrahovConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 26, 0),
                "143", toStrahovConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 32, 0),
                "149", toStrahovConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 41, 0),
                "143", toStrahovConnection,
            ),
        )
    }

    @Test
    fun weekEndStrahovDejvicka() = runTest {
        val repo: PIDRepo = PIDRepoImpl(database)

        val date = LocalDate(2022, Month.FEBRUARY, 5)
        val dateTime = LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 0, 0)

        val toDejvickaConnection = TransportConnections.strahovDejvicka(true)
        val toStrahovConnection = TransportConnections.strahovDejvicka(false)

        val toDejvicka = repo.getData(dateTime, toDejvickaConnection).take(5)
        val toStrahov = repo.getData(dateTime, toStrahovConnection).take(5)

        toDejvicka shouldContainInOrder listOf(
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 19, 0),
                "149", toDejvickaConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 38, 0),
                "143", toDejvickaConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 52, 0),
                "149", toDejvickaConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 9, 8, 0),
                "143", toDejvickaConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 9, 22, 0),
                "149", toDejvickaConnection,
            ),
        )

        toStrahov shouldContainInOrder listOf(
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 0, 0),
                "149", toStrahovConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 20, 0),
                "143", toStrahovConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 34, 0),
                "149", toStrahovConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 8, 51, 0),
                "143", toStrahovConnection,
            ),
            DepartureInfo(
                LocalDateTime(date.year, date.month, date.dayOfMonth, 9, 4, 0),
                "149", toStrahovConnection,
            ),
        )
    }
}