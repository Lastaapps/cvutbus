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
    kotlin("multiplatform")
    id(Plugins.LIBRARY)
}

group = App.GROUP
version = App.VERSION_NAME

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    sourceSets.all {
        languageSettings.apply {
            languageVersion = Versions.KOTLIN_LANGUAGE_VERSION
            apiVersion = Versions.KOTLIN_LANGUAGE_VERSION
        }
    }
    android {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.JVM_TARGET
        }
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.JVM_TARGET
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":storage:database"))

                api(Libs.KOTLINX_DATETIME)
                api(Libs.KM_LOGGING)
                implementation(Tests.KOTEST_ASSERTION)
                implementation(Libs.KOTLIN_COROUTINES)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(Tests.COROUTINES)
            }
        }
        val androidMain by getting {
            dependencies {
            }
        }
        val androidTest by getting {
            dependencies {
            }
        }
        val desktopMain by getting {
            dependencies {
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(project.dependencies.platform(Tests.JUNIT_BOM))
                implementation(Tests.JUNIT_JUPITER)
            }
        }
    }
}

android {
    compileSdk = App.COMPILE_SDK

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = App.MIN_SDK
        targetSdk = App.TARGET_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = Versions.JAVA
        targetCompatibility = Versions.JAVA
    }
    dependencies {
        coreLibraryDesugaring(Libs.DESUGARING)
    }
}
