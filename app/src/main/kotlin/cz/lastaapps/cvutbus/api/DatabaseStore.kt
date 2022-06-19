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

package cz.lastaapps.cvutbus.api

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cz.lastaapps.cvutbus.format
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DatabaseStore constructor(app: Application) {

    companion object {
        private val Context.infoStore: DataStore<Preferences> by preferencesDataStore("database_info")

        private val minAppVersion = longPreferencesKey("min_app_version")
        private val dataReleaseDate = stringPreferencesKey("data_release_date")
        private val dataValidUntil = stringPreferencesKey("data_valid_until")
        private val fileSize = longPreferencesKey("file_size")

        private val dateFormat = DateTimeFormatter.ISO_DATE

        private const val defaultDataSourceDir =
            "https://raw.githubusercontent.com/Lastaapps/cvutbus/cloud_data/cloud_data/"
        private const val configFileName = "config.json"
        private const val databaseFileName = "piddatabase.db"
    }

    private val store = app.infoStore

    val databaseInfo: Flow<DatabaseInfo?>
        get() = store.data.map {
            DatabaseInfo(
                it[minAppVersion] ?: return@map null,
                it[dataReleaseDate]?.toDate() ?: return@map null,
                it[dataValidUntil]?.toDate() ?: return@map null,
                it[fileSize] ?: return@map null,
            )
        }

    suspend fun setDatabaseInfo(databaseInfo: DatabaseInfo) {
        store.edit {
            it[minAppVersion] = databaseInfo.minAppVersion
            it[dataReleaseDate] = databaseInfo.dataReleaseDate.format(dateFormat)
            it[dataValidUntil] = databaseInfo.dataValidUntil.format(dateFormat)
            it[fileSize] = databaseInfo.fileSize
        }
    }

    private fun String.toDate(): LocalDate =
        java.time.LocalDate.parse(this, dateFormat).toKotlinLocalDate()


    private val dataSourceKey = stringPreferencesKey("data_source_config")
    private val dataSourceDir: Flow<String>
        get() = store.data.map { it[dataSourceKey] ?: defaultDataSourceDir }

    val dataSourceConfig: Flow<String>
        get() = dataSourceDir.map { it + configFileName }
    val dataSourceDatabase: Flow<String>
        get() = dataSourceDir.map { it + databaseFileName }


    private val lastUpdatedKey = stringPreferencesKey("last_updated")
    private val lastUpdatedDateFormat = DateTimeFormatter.ISO_DATE_TIME
    val lastUpdated: Flow<ZonedDateTime?>
        get() = store.data.map { pref ->
            pref[lastUpdatedKey]?.let { ZonedDateTime.parse(it, lastUpdatedDateFormat) }
        }

    suspend fun setLastUpdated(date: ZonedDateTime = ZonedDateTime.now()) {
        store.edit { it[lastUpdatedKey] = date.format(lastUpdatedDateFormat) }
    }
}
