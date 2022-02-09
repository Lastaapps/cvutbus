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

package cz.lastaapps.cvutbus

import android.content.Context
import android.text.format.DateFormat
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun kotlinx.datetime.LocalDate.format(formatter: DateTimeFormatter): String =
    toJavaLocalDate().format(formatter)

fun kotlinx.datetime.LocalDateTime.format(formatter: DateTimeFormatter): String =
    toJavaLocalDateTime().format(formatter)


fun Context.uses24Hour(): Boolean = DateFormat.is24HourFormat(this)

fun LocalTime.localizedFormat(context: Context): String =
    localizedFormat(context.uses24Hour())

fun LocalTime.localizedFormat(use24: Boolean): String {
    val patter = if (use24) "H:mm" else "h:mm a"
    val formatter = DateTimeFormatter.ofPattern(patter)
    return format(formatter)
}

fun kotlinx.datetime.LocalDateTime.localizedFormat(context: Context): String =
    toJavaLocalDateTime().localizedFormat(context)

fun kotlinx.datetime.LocalDateTime.localizedFormat(use24: Boolean): String =
    toJavaLocalDateTime().localizedFormat(use24)

fun java.time.LocalDateTime.localizedFormat(context: Context): String =
    toLocalTime().localizedFormat(context)

fun java.time.LocalDateTime.localizedFormat(use24: Boolean): String =
    toLocalTime().localizedFormat(use24)


fun kotlin.time.Duration.toHours() = inWholeHours.toInt()
fun kotlin.time.Duration.toMinutes() = (inWholeMinutes % 60).toInt()
fun kotlin.time.Duration.toSeconds() = (inWholeSeconds % 60).toInt()

fun kotlin.time.Duration.countdownFormat(showSeconds: Boolean): String {
    val hours = toHours()
    val minutes = toMinutes()
    val seconds = toSeconds()

    return if (!showSeconds) {
        "%d:%02d".format(hours, minutes)
    } else {
        if (hours != 0) {
            "%d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%d:%02d".format(minutes, seconds)
        }
    }
}

fun kotlin.time.Duration.toLocalTime(): LocalTime = LocalTime.of(
    toHours(), toMinutes(), toSeconds()
)


