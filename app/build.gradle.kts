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
    id(Plugins.APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.DAGGER_HILT)
    id(Plugins.KSP)
    id(Plugins.KAPT)
    id(Plugins.ABOUT_LIBRARIES)
}

android {
    compileSdk = App.COMPILE_SDK

    defaultConfig {
        applicationId = App.APP_ID
        minSdk = App.MIN_SDK
        targetSdk = App.TARGET_SDK
        versionCode = App.VERSION_CODE
        versionName = App.VERSION_NAME

        resourceConfigurations += setOf("en", "cs")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false

            extra.set("alwaysUpdateBuildId", false)
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    kotlinOptions {
        jvmTarget = Versions.JVM_TARGET
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.COMPOSE_COMPILER
    }
    packagingOptions {
        resources {
            excludes += "META-INF/**"
        }
    }
}

dependencies {
    coreLibraryDesugaring(Libs.DESUGARING)

    implementation(project(":entity"))
    implementation(project(":storage:database"))
    implementation(project(":storage:repo"))
    implementation(project(":lastaapps:common"))

    implementation(Libs.APPCOMPAT)
    implementation(Libs.SPLASHSCREEN)
    implementation(Libs.MATERIAL)
    implementation(Libs.CORE)
    implementation(Libs.LIFECYCLE)
    implementation(Libs.LIFECYCLE_LIVEDATA)
    implementation(Libs.STARTUP)
    implementation(Libs.VECTOR_DRAWABLES)
    implementation(Libs.WINDOW_MANAGER)
    implementation(Libs.WORK)

    implementation(Libs.DAGGER_HILT)
    implementation(Libs.DATASTORE)
    implementation(Libs.HILT_COMMON)
    implementation(Libs.HILT_VIEWMODEL)
    implementation(Libs.HILT_WORK)
    implementation(Libs.HILT_NAVIGATION_COMPOSE)
    kapt(Libs.DAGGER_HILT_COMPILER)
    kapt(Libs.HILT_COMPILER)

    implementation(Libs.KOTLINX_DATETIME)
    implementation(Libs.ABOUT_LIBRARIES_CORE)
    implementation(Libs.KTOR_CORE)
    implementation(Libs.KTOR_CIO)

    initCompose(useMaterial2 = true)
}

fun DependencyHandler.initCompose(useMaterial2: Boolean = false, useMaterial3: Boolean = true) {
    implementation(Libs.COMPOSE_ACTIVITY)
    implementation(Libs.COMPOSE_ANIMATION)
    implementation(Libs.COMPOSE_CONSTRAINTLAYOUT)
    implementation(Libs.COMPOSE_FOUNDATION)
    implementation(Libs.COMPOSE_ICONS_EXTENDED)
    if (useMaterial2)
        implementation(Libs.COMPOSE_MATERIAL)
    if (useMaterial3)
        implementation(Libs.COMPOSE_MATERIAL_3)
    implementation(Libs.COMPOSE_NAVIGATION)
    debugImplementation(Libs.COMPOSE_TOOLING)
    implementation(Libs.COMPOSE_UI)
    implementation(Libs.COMPOSE_VIEWMODEL)

    implementation(Libs.ACCOMPANIST_DRAWABLE_PAINTERS)
    implementation(Libs.ACCOMPANIST_FLOW_LAYOUTS)
    implementation(Libs.ACCOMPANIST_INSETS)
    implementation(Libs.ACCOMPANIST_NAVIGATION_ANIMATION)
    implementation(Libs.ACCOMPANIST_NAVIGATION_MATERIAL)
    implementation(Libs.ACCOMPANIST_PAGER)
    implementation(Libs.ACCOMPANIST_PERMISSION)
    implementation(Libs.ACCOMPANIST_PLACEHOLDER)
    implementation(Libs.ACCOMPANIST_SWIPE_TO_REFRESH)
    implementation(Libs.ACCOMPANIST_SYSTEM_UI)
}