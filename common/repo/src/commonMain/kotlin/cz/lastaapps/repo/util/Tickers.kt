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

package cz.lastaapps.repo.util

import cz.lastaapps.core.util.roundToSeconds
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

suspend fun secondsTicker(
    getNow: () -> Instant = { Clock.System.now() },
    onTick: (Instant) -> Unit,
) = secondsTickerStopAble(getNow) { onTick(it); true }

suspend fun secondsTickerStopAble(
    getNow: () -> Instant = { Clock.System.now() },
    onTick: (Instant) -> Boolean,
) {
    val safetyOffset = 10
    while (true) {
        val now = getNow().roundToSeconds()
        if (!onTick(now)) break
        delay(1_000 - now.toEpochMilliseconds() % 1_000 + safetyOffset)
    }
}

suspend fun minutesTicker(
    getNow: () -> Instant = { Clock.System.now() },
    onTick: (Instant) -> Unit,
) = minutesTickerStopAble(getNow) { onTick(it); true }

suspend fun minutesTickerStopAble(
    getNow: () -> Instant = { Clock.System.now() },
    onTick: (Instant) -> Boolean,
) {
    val safetyOffset = 10
    while (true) {
        val now = getNow().roundToSeconds()
        if (!onTick(now)) break
        delay(60_000 - now.toEpochMilliseconds() % 60_000 + safetyOffset)
    }
}
