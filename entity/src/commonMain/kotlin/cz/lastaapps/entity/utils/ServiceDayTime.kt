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


package cz.lastaapps.entity.utils

import io.kotest.matchers.ints.shouldBeInRange

/**
 * Represents time in service day described by gtfs specification
 * https://developers.google.com/transit/gtfs/reference/#term_definitions
 */
data class ServiceDayTime(val hours: Int, val minutes: Int, val seconds: Int) {

    init {
        hours shouldBeInRange 0..47
        minutes shouldBeInRange 0..59
        seconds shouldBeInRange 0..59
    }

    fun toDaySeconds(): Int {
        return hours * 3600 + minutes * 60 + seconds
    }

    companion object {

        fun fromDaySeconds(total: Int): ServiceDayTime {
            var mTotal = total
            val hours: Int = mTotal / 3600
            mTotal -= hours * 3600
            val minutes: Int = mTotal / 60
            mTotal -= minutes * 60
            val seconds: Int = mTotal

            return ServiceDayTime(hours, minutes, seconds)
        }
    }
}