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

package cz.lastaapps.plugin.android

import cz.lastaapps.extensions.*
import cz.lastaapps.plugin.BasePlugin
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

class AndroidKoinConvention : BasePlugin({
    pluginManager {
        alias(libs.plugins.google.ksp)
    }

    kotlinExtension.apply {
        sourceSets.all {
            kotlin.srcDir("build/generated/ksp/$name/kotlin")
        }
    }

    dependencies {
        implementation(libs.koin.core)
        implementation(libs.koin.android.core)

        implementation(libs.koin.annotations)
        ksp(libs.koin.annotations.compiler)

        testImplementation(libs.koin.test)
    }
})
