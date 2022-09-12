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

package cz.lastaapps.repo.data.preferences

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import cz.lastaapps.repo.util.getSerializableOrNull
import cz.lastaapps.repo.util.getSerializableOrNullFlow
import cz.lastaapps.repo.util.putSerializable
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi

internal interface ConfigPreferences {
    suspend fun getDownloadUrl(): String

    suspend fun getLastChecked(): LocalDate?
    fun getLastCheckedFlow(): Flow<LocalDate?>

    suspend fun getReleaseDate(): LocalDate?
    fun getReleaseDateFlow(): Flow<LocalDate?>

    suspend fun getValidUntil(): LocalDate?
    fun getValidUntilFlow(): Flow<LocalDate?>

    suspend fun updateLastChecked(lastChecked: LocalDate)
    suspend fun updateData(releaseDate: LocalDate, validUntil: LocalDate)
}

@ExperimentalSerializationApi
@OptIn(ExperimentalSettingsApi::class)
internal class ConfigPreferencesImpl(
    private val set: FlowSettings,
) : ConfigPreferences {

    companion object {
        private const val downloadKey = "download_url"
        private const val defaultDownloadUrl =
            "https://raw.githubusercontent.com/Lastaapps/cvutbus/cloud_data/cloud_data/config.json"
        private const val lastCheckedKey = "last_checked"
        private const val releaseKey = "release"
        private const val validUntilKey = "valid_until"
    }

    private val ldSer = LocalDate.serializer()

    override suspend fun getDownloadUrl(): String =
        set.getString(downloadKey, defaultDownloadUrl)


    // ---------------------------------------------------------------------------------------------
    override suspend fun getLastChecked(): LocalDate? =
        set.getSerializableOrNull(ldSer, lastCheckedKey)

    override fun getLastCheckedFlow(): Flow<LocalDate?> =
        set.getSerializableOrNullFlow(ldSer, lastCheckedKey)

    override suspend fun updateLastChecked(lastChecked: LocalDate) =
        set.putSerializable(ldSer, lastCheckedKey, lastChecked)


    // ---------------------------------------------------------------------------------------------
    override suspend fun getReleaseDate(): LocalDate? =
        set.getSerializableOrNull(ldSer, releaseKey)

    override fun getReleaseDateFlow(): Flow<LocalDate?> =
        set.getSerializableOrNullFlow(ldSer, releaseKey)

    override suspend fun getValidUntil(): LocalDate? =
        set.getSerializableOrNull(ldSer, validUntilKey)

    override fun getValidUntilFlow(): Flow<LocalDate?> =
        set.getSerializableOrNullFlow(ldSer, validUntilKey)

    override suspend fun updateData(releaseDate: LocalDate, validUntil: LocalDate) {
        set.putSerializable(ldSer, releaseKey, releaseDate)
        set.putSerializable(ldSer, validUntilKey, validUntil)
    }
}
