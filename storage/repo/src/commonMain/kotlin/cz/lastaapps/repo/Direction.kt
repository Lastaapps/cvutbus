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

package cz.lastaapps.repo

sealed class Direction(private val isIn: Boolean) {
    object Outbound : Direction(false) {
        override fun toString(): String = "Outbound"
    }

    object Inbound : Direction(true) {
        override fun toString(): String = "Inbound"
    }

    val toBool get() = isIn

    companion object {
        fun fromBoolean(boolean: Boolean) =
            if (boolean == Outbound.toBool) Outbound else Inbound
    }
}