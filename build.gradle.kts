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

buildscript {
    dependencies {
        classpath(Classpath.DAGGER_HILT)
    }
}
plugins {
    id(Plugins.APPLICATION) version Versions.GRADLE apply false
    id(Plugins.LIBRARY) version Versions.GRADLE apply false

    id(Plugins.KOTLIN) version Versions.KOTLIN apply false
    id(Plugins.KSP) version Versions.KSP apply false
    id(Plugins.KOTLIN_JVM) version Versions.KOTLIN apply false

    id(Plugins.ABOUT_LIBRARIES) version Versions.ABOUT_LIBRARIES apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

allprojects {
    afterEvaluate {
        // Remove log pollution until Android support in KMP improves.
        // https://discuss.kotlinlang.org/t/disabling-androidandroidtestrelease-source-set-in-gradle-kotlin-dsl-script/21448/5
        project.extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()
            ?.let { kmpExt ->
                kmpExt.sourceSets.removeAll { sourceSet ->
                    setOf(
                        "androidAndroidTestRelease",
                        "androidTestFixtures",
                        "androidTestFixturesDebug",
                        "androidTestFixturesRelease",
                    ).contains(sourceSet.name)
                }
            }
    }
}