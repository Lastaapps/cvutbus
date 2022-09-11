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
import com.russhwolf.settings.coroutines.toBlockingSettings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi

internal interface ConfigPreferences {
    suspend fun getDownloadUrl(): String

    suspend fun getLastChecked(): LocalDate?
    suspend fun getReleaseDate(): LocalDate?
    suspend fun getValidUntil(): LocalDate?
    suspend fun updateLastChecked(lastChecked: LocalDate)
    suspend fun updateData(releaseDate: LocalDate, validUntil: LocalDate)
}

// TODO resolve flow outputs
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

    private val blk = set.toBlockingSettings()
    private val ldSer = LocalDate.serializer()

    override suspend fun getDownloadUrl(): String =
        set.getString(downloadKey, defaultDownloadUrl)


    // ---------------------------------------------------------------------------------------------
    override suspend fun getLastChecked(): LocalDate? =
        blk.decodeValueOrNull(ldSer, lastCheckedKey)

    override suspend fun updateLastChecked(lastChecked: LocalDate) =
        blk.encodeValue(ldSer, lastCheckedKey, lastChecked)


    // ---------------------------------------------------------------------------------------------
    override suspend fun getReleaseDate(): LocalDate? =
        blk.decodeValueOrNull(ldSer, releaseKey)

    override suspend fun getValidUntil(): LocalDate? =
        blk.decodeValueOrNull(ldSer, validUntilKey)

    override suspend fun updateData(releaseDate: LocalDate, validUntil: LocalDate) {
        blk.encodeValue(ldSer, releaseKey, releaseDate)
        blk.encodeValue(ldSer, validUntilKey, validUntil)
    }
}
