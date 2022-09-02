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

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import cz.lastaapps.extensions.alias
import cz.lastaapps.extensions.implementation
import cz.lastaapps.extensions.libs
import cz.lastaapps.extensions.pluginManager
import cz.lastaapps.plugin.BasePlugin
import cz.lastaapps.plugin.android.common.KotlinBaseConvention
import cz.lastaapps.plugin.android.config.configureKotlinAndroid
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies


class AndroidAppConvention : BasePlugin({
    pluginManager {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
    }

    extensions.configure<BaseAppModuleExtension> {
        configureKotlinAndroid(this)

        defaultConfig {
            targetSdk = libs.versions.sdk.target.get().toInt()
            multiDexEnabled = true

            resourceConfigurations += setOf("en", "cs")
        }

        buildFeatures {
            buildConfig = true
        }

        packagingOptions {
            resources {
                excludes += "META-INF/domain.kotlin_module"
                excludes += "META-INF/data.kotlin_module"
                excludes += "META-INF/infrastructure.kotlin_module"
                excludes += "META-INF/presentation.kotlin_module"
                excludes += "META-INF/LICENSE.md"
                excludes += "META-INF/LICENSE-notice.md"

                // for JNA and JNA-platform
                excludes += "META-INF/AL2.0"
                excludes += "META-INF/LGPL2.1"
                // for byte-buddy
                excludes += "META-INF/licenses/ASM"
                pickFirsts += "win32-x86-64/attach_hotspot_windows.dll"
                pickFirsts += "win32-x86/attach_hotspot_windows.dll"
            }
        }

    }

    apply<KotlinBaseConvention>()
    apply<AndroidKoinConvention>()
    apply<AndroidBaseConvention>()

    dependencies {
        implementation(libs.google.material)
        implementation(libs.androidx.splashscreen)
        implementation(libs.androidx.startup)
        implementation(libs.androidx.vectorDrawables)
    }
})
