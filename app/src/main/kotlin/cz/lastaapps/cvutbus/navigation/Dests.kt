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

package cz.lastaapps.cvutbus.navigation

interface Dests {
    object Routes {

        const val pid = "pid"
        const val settings = "settings"
        const val osturak = "osturak"
        const val license = "license"
        const val privacyPolicy = "privacy_policy"

        const val starting = pid
    }

    //object Args
}

fun String.routesEquals(other: String): Boolean {
    return substringBeforeLast("?") == (other.substringBeforeLast("?"))
}
