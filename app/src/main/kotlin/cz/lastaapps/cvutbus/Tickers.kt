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

import cz.lastaapps.entity.utils.roundToSeconds
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

suspend fun secondTicker(getNow: () -> Instant = ::getRoundedNow, onTick: (Instant) -> Unit) {
    secondTickerStopAble(getNow) { onTick(it); true }
}

suspend fun secondTickerStopAble(
    getNow: () -> Instant = ::getRoundedNow,
    onTick: (Instant) -> Boolean
) {
    val safetyOffset = 10
    var keep = onTick(getNow())
    while (keep) {
        delay(1000 - System.currentTimeMillis() % 1000 + safetyOffset)
        keep = onTick(getNow())
    }
}

suspend fun minuteTicker(getNow: () -> Instant = ::getRoundedNow, onTick: (Instant) -> Unit) {
    minuteTickerStopAble(getNow) { onTick(it); true }
}

suspend fun minuteTickerStopAble(
    getNow: () -> Instant = ::getRoundedNow,
    onTick: (Instant) -> Boolean
) {
    val safetyOffset = 10
    var keep = onTick(getNow())
    while (keep) {
        delay(60_000 - System.currentTimeMillis() % 60_000 + safetyOffset)
        keep = onTick(getNow())
    }
}

fun getRoundedNow() = Clock.System.now().roundToSeconds()