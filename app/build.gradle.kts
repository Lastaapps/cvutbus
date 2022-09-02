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

plugins {
    id(libs.plugins.lastaapps.android.app.compose.get().pluginId)
    alias(libs.plugins.aboutLibraries)
}

android {
    namespace = "cz.lastaapps.cvutbus"

    defaultConfig {
        applicationId = "cz.lastaapps.cvutbus"

        //have to be specified explicitly for FDroid to work
        versionCode = 1000000 // 1x major . 2x minor . 2x path . 2x build diff
        versionName = "1.0.0"
    }
}

dependencies {

    implementation(projects.entity)
    implementation(projects.common.database)
    implementation(projects.common.repo)
    implementation(projects.lastaapps.common)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.windowManager)

    implementation(libs.androidx.work)
    implementation(libs.androidx.lifecycle.livedata) // asFlow()
    implementation(libs.koin.android.workmanager)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    implementation(libs.aboutLibraries.core)
}
