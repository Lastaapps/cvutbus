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

import cz.lastaapps.database.domain.model.calendar.ServiceDayTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus

internal fun serviceToNormalDateTime(date: LocalDate, time: ServiceDayTime): LocalDateTime {
    val hours = time.hours
    val plusDays = hours / 24
    val newDate = date.plus(plusDays, DateTimeUnit.DAY)
    val newHours = hours - plusDays * 24
    return LocalDateTime(
        newDate.year, newDate.month, newDate.dayOfMonth,
        newHours, time.minutes, time.seconds, 0
    )
}