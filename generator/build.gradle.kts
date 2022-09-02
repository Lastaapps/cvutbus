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
    id(libs.plugins.lastaapps.jvm.app.get().pluginId)
}

application {
    mainClass.set("cz.lastaapps.generator.MainKt")
}

dependencies {
    implementation(projects.entity)
    implementation(projects.common.database)
    implementation(projects.common.repo)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
//    implementation(libs.ktor.client.java)
}

tasks.test {
    useJUnitPlatform()
    maxHeapSize = "1G"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "cz.lastaapps.generator.MainKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    configurations.getByName("runtimeClasspath").forEach {
        from(if (it.isDirectory) it else zipTree(it))
    }
}
