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

package cz.lastaapps.repo.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import cz.lastaapps.repo.data.UpdateRepositoryImpl
import cz.lastaapps.repo.data.api.ConfigApiImpl
import cz.lastaapps.repo.data.preferences.ConfigPreferences
import cz.lastaapps.repo.data.preferences.ConfigPreferencesImpl
import cz.lastaapps.repo.domain.UpdateRepository
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(
    ExperimentalSettingsImplementation::class, ExperimentalSettingsApi::class,
    ExperimentalSerializationApi::class
)
actual val platformModule = module {
    single {
        ConfigPreferencesImpl(
            DataStoreSettings(androidApplication().updateDataStore)
        )
    } bind ConfigPreferences::class

    singleOf(::UpdateRepositoryImpl) { bind<UpdateRepository>() }
    factoryOf(::ConfigApiImpl)
}

private val Context.updateDataStore: DataStore<Preferences> by preferencesDataStore(name = "update")