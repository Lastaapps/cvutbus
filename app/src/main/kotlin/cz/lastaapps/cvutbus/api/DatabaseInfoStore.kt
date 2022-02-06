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

package cz.lastaapps.cvutbus.api

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInfoStore @Inject constructor(app: Application) {

    companion object {
        private val Context.infoStore: DataStore<Preferences> by preferencesDataStore("database_info")

        private val minAppVersion = longPreferencesKey("min_app_version")
        private val dataReleaseDate = stringPreferencesKey("data_release_date")
        private val dataValidUntil = stringPreferencesKey("data_valid_until")
        private val fileSize = longPreferencesKey("file_size")

        private val dateFormat = DateTimeFormatter.ISO_DATE
    }

    private val store = app.infoStore

    suspend fun getDatabaseInfo(): DatabaseInfo? {
        return store.data.map {
            DatabaseInfo(
                it[minAppVersion] ?: return@map null,
                it[dataReleaseDate]?.toDate() ?: return@map null,
                it[dataValidUntil]?.toDate() ?: return@map null,
                it[fileSize] ?: return@map null,
            )
        }.first()
    }

    suspend fun setDatabaseInfo(databaseInfo: DatabaseInfo) {
        store.edit {
            it[minAppVersion] = databaseInfo.minAppVersion
            it[dataReleaseDate] = databaseInfo.dataReleaseDate.toJavaLocalDate().format(dateFormat)
            it[dataValidUntil] = databaseInfo.dataValidUntil.toJavaLocalDate().format(dateFormat)
            it[fileSize] = databaseInfo.fileSize
        }
    }

    private fun String.toDate(): LocalDate =
        java.time.LocalDate.parse(this, dateFormat).toKotlinLocalDate()
}