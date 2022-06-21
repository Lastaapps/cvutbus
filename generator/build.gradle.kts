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
    application
    id(Plugins.KOTLIN_JVM)
}

group = App.GROUP
version = App.VERSION_NAME

application {
}

dependencies {
    implementation(project(":storage:database"))
    implementation(project(":storage:repo"))

    implementation(Libs.KOTLINX_DATETIME)
    implementation(Tests.KOTEST_ASSERTION)
    implementation(Libs.KM_LOGGING)

    implementation(Libs.KOTLIN_COROUTINES)
    implementation(Libs.KTOR_CORE)
    //implementation(Libs.KTOR_CIO)
    implementation("io.ktor:ktor-client-java:${Versions.KTOR}")

    // required by KM Logging for the JVM target
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-core:1.2.7")
    implementation("ch.qos.logback:logback-classic:1.2.7")

    testImplementation(kotlin("test"))
    testImplementation(Tests.COROUTINES)
    testImplementation(project.dependencies.platform(Tests.JUNIT_BOM))
    testImplementation(Tests.JUNIT_JUPITER)
    testRuntimeOnly(Tests.JUNIT_ENGINE)
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
