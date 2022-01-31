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

package cz.lastaapps.database.testdata

import cz.lastaapps.entity.Calendar
import cz.lastaapps.entity.ServiceDays
import cz.lastaapps.entity.ServiceId
import kotlinx.datetime.LocalDate
import java.io.BufferedReader
import java.io.InputStreamReader

object CalendarParser {

    fun parse(): List<Calendar> {
        val stream = ResourceOpener.openResource("calendar")
        val buffered = BufferedReader(InputStreamReader(stream))

        buffered.readLine()
        return buffered.readLines().map { line ->
            val split = line.split(",")
            Calendar(
                ServiceId(split[0]),
                ServiceDays.Companion.fromDays(split.drop(1).take(7).map { it == "1" }),
                split[8].toLocalDate(),
                split[9].toLocalDate(),
            )
        }
    }

    private fun String.toLocalDate(): LocalDate {
        val num = toInt()
        val year = num / 10_000
        val month = num % 10_000 / 100
        val seconds = num % 100
        return LocalDate(year, month, seconds)
    }
}