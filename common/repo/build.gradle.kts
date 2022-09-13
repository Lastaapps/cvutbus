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
    id(libs.plugins.lastaapps.kmp.library.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "cz.lastaapps.repo"
}

dependencies {
    commonMainImplementation(projects.base)
    commonMainImplementation(projects.common.database)
    commonMainImplementation(projects.common.core)

    commonMainImplementation(libs.kotlinx.serializationJson)

    commonMainImplementation(libs.ktor.client.core)
    androidMainImplementation(libs.ktor.client.cio)
    jvmMainImplementation(libs.ktor.client.cio)

    commonMainImplementation(libs.russhwolf.settins.core)
    commonMainImplementation(libs.russhwolf.settins.coroutines)
    androidMainImplementation(libs.russhwolf.settins.datastore)
    androidMainImplementation(libs.androidx.datastore)
}
