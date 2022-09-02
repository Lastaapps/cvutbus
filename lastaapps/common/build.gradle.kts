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

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    id(libs.plugins.lastaapps.android.library.core.get().pluginId)
}

android {
    namespace = "cz.lastaapps.common"
    defaultConfig {

        val buildDate: String = ZonedDateTime.now()
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toLocalDate()
            .format(DateTimeFormatter.ISO_DATE)

        buildConfigField("java.lang.String", "BUILD_DATE", "\"$buildDate\"")
    }
    buildFeatures {
        buildConfig = true
    }
}
