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

package cz.lastaapps.database.domain.model.calendar

import cz.lastaapps.core.util.index
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/**
 * Stores in which days of week a service operates
 */
@JvmInline
value class ServiceDays private constructor(private val data: Int) {
    companion object {

        fun fromDays(list: List<Boolean>): ServiceDays {
            check(list.size == 7)
            return fromDays(
                list[0], list[1], list[2], list[3], list[4], list[5], list[6],
            )
        }

        fun fromDays(
            monday: Boolean = false, tuesday: Boolean = false, wednesday: Boolean = false,
            thursday: Boolean = false, friday: Boolean = false, saturday: Boolean = false,
            sunday: Boolean = false,
        ): ServiceDays {
            var data = 0

            listOf(
                monday to DayOfWeek.MONDAY,
                tuesday to DayOfWeek.TUESDAY,
                wednesday to DayOfWeek.WEDNESDAY,
                thursday to DayOfWeek.THURSDAY,
                friday to DayOfWeek.FRIDAY,
                saturday to DayOfWeek.SATURDAY,
                sunday to DayOfWeek.SUNDAY,
            ).forEach { pair ->
                if (pair.first) data += 1 shl pair.second.index
            }

            return ServiceDays(data)
        }

        fun fromDatabase(stored: Int) = ServiceDays(stored)
    }

    fun hasDay(day: DayOfWeek): Boolean = data and (1 shl day.index) != 0

    fun toDatabase(): Int = data

    val days: List<Boolean>
        get() = listOf(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY,
        ).map { hasDay(it) }

    override fun toString(): String {
        return days.joinToString { if (it) "1" else "0" }
    }
}

fun ServiceDays.hasDay(day: LocalDate): Boolean = hasDay(day.dayOfWeek)
