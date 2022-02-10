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

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Represents time in service day described by gtfs specification
 * https://developers.google.com/transit/gtfs/reference/#term_definitions
 */
@JvmInline
value class ServiceDayTime(val duration: Duration) : Comparable<ServiceDayTime> {

    init {
//        hours shouldBeInRange 0..47
//        minutes shouldBeInRange 0..59
//        seconds shouldBeInRange 0..59
    }

    val hours: Int get() = duration.toHours()
    val minutes: Int get() = duration.toMinutes()
    val seconds: Int get() = duration.toSeconds()

    val daySeconds: Long get() = duration.inWholeSeconds

    companion object {
        fun fromDaySeconds(seconds: Int): ServiceDayTime {
            return ServiceDayTime(seconds.seconds)
        }

        fun of(hours: Int, minutes: Int, seconds: Int): ServiceDayTime =
            ServiceDayTime((hours * 3600 + minutes * 60 + seconds).seconds)
    }

    override fun compareTo(other: ServiceDayTime): Int {
        return duration.compareTo(other.duration)
    }
}