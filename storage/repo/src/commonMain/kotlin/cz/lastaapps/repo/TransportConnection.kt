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

import cz.lastaapps.entity.StopName

data class TransportConnection(
    private val stop1: StopName,
    private val stop2: StopName,
    val direction: Direction,
) {
    val from: StopName
        get() = if (direction == Direction.Outbound) stop1 else stop2

    val to: StopName
        get() = if (direction != Direction.Outbound) stop1 else stop2

    companion object {
        fun fromStopPair(stopPair: StopPair, direction: Direction) =
            TransportConnection(stopPair.stop1, stopPair.stop2, direction)
    }
}