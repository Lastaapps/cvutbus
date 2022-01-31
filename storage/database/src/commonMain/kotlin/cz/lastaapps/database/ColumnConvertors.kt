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

import com.squareup.sqldelight.ColumnAdapter
import cz.lastaapps.entity.*
import cz.lastaapps.entity.utils.ServiceDayTime
import kotlinx.datetime.LocalDate

internal object ColumnConvertors {

    val direction = object : ColumnAdapter<Direction, Long> {
        override fun decode(databaseValue: Long): Direction {
            return if (databaseValue.toInt() == Direction.Inbound.id) Direction.Inbound else Direction.Outbound
        }

        override fun encode(value: Direction): Long {
            return value.id.toLong()
        }
    }

    val routeId = object : ColumnAdapter<RouteId, Long> {
        override fun decode(databaseValue: Long): RouteId {
            return RouteId(databaseValue.toInt())
        }

        override fun encode(value: RouteId): Long {
            return value.id.toLong()
        }
    }

    val serviceDays = object : ColumnAdapter<ServiceDays, Long> {
        override fun decode(databaseValue: Long): ServiceDays {
            return ServiceDays.fromDatabase(databaseValue.toInt())
        }

        override fun encode(value: ServiceDays): Long {
            return value.toDatabase().toLong()
        }
    }

    val serviceId = object : ColumnAdapter<ServiceId, String> {
        override fun decode(databaseValue: String): ServiceId {
            return ServiceId(databaseValue)
        }

        override fun encode(value: ServiceId): String {
            return value.id
        }
    }

    val stopId = object : ColumnAdapter<StopId, String> {
        override fun decode(databaseValue: String): StopId {
            return StopId(databaseValue)
        }

        override fun encode(value: StopId): String {
            return value.id
        }
    }

    val tripId = object : ColumnAdapter<TripId, String> {
        override fun decode(databaseValue: String): TripId {
            return TripId(databaseValue)
        }

        override fun encode(value: TripId): String {
            return value.id
        }
    }

    val localTime = object : ColumnAdapter<ServiceDayTime, Long> {
        override fun decode(databaseValue: Long): ServiceDayTime {
            return ServiceDayTime.fromDaySeconds(databaseValue.toInt())
        }

        override fun encode(value: ServiceDayTime): Long {
            return value.toDaySeconds().toLong()
        }
    }

    val localDate = object : ColumnAdapter<LocalDate, Long> {
        override fun decode(databaseValue: Long): LocalDate {
            val year = databaseValue / 10_000
            val month = databaseValue % 10_000 / 100
            val day = databaseValue % 100
            return LocalDate(year.toInt(), month.toInt(), day.toInt())
        }

        override fun encode(value: LocalDate): Long {
            return (value.year * 10_000 + value.monthNumber * 100 + value.dayOfMonth).toLong()
        }
    }
}
