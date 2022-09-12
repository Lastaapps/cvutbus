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

package cz.lastaapps.repo.data

import cz.lastaapps.base.Resultus
import cz.lastaapps.base.errors.UpdateErrors
import cz.lastaapps.base.toResultus
import cz.lastaapps.base.unitResultus
import cz.lastaapps.database.domain.UpdateDataSource
import cz.lastaapps.database.util.CET
import cz.lastaapps.repo.data.api.ConfigApi
import cz.lastaapps.repo.data.preferences.ConfigPreferences
import cz.lastaapps.repo.domain.UpdateRepository
import cz.lastaapps.repo.domain.model.DataVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

internal class UpdateRepositoryImpl(
    private val pref: ConfigPreferences,
    private val clock: Clock = Clock.System,
    private val configApi: ConfigApi,
    private val updateDest: UpdateDataSource,
) : UpdateRepository {

    override suspend fun updateFromRepo(force: Boolean): Resultus<Unit> {
        //check if needed
        val now = clock.now().toLocalDateTime(CET).date

        pref.getValidUntil()?.let { validUntil ->
            if (!force && validUntil >= now.plus(2, DateTimeUnit.DAY)
            ) return unitResultus
        }

        // download config
        val remoteConfig = when (val res =
            configApi.downloadConfig(pref.getDownloadUrl())) {
            is Resultus.Error -> return res
            is Resultus.Success -> res.data
        }

        pref.updateLastChecked(Clock.System.now().toLocalDateTime(CET).date)

        // compare config
        if (remoteConfig.v1 == null)
            return UpdateErrors.VersionNoLongerSupported().toResultus()
        pref.getReleaseDate()?.let { release ->
            if (release >= remoteConfig.v1.releaseDate)
                return UpdateErrors.NoNewDataAvailable().toResultus()
        }

        // download the db and create a temp file
        val updateSrc = when (val res = configApi.downloadDatabase(remoteConfig.v1.link)) {
            is Resultus.Error -> return res
            is Resultus.Success -> res.data
        }

        updateDest.inTransaction {
            updateDest.updateFromAnother(updateSrc)
        }

        with(remoteConfig.v1) {
            pref.updateData(releaseDate, validUntil)
        }

        configApi.deleteDatabase()

        return unitResultus
    }

    override suspend fun getConfig(): Flow<DataVersion> = with(pref) {
        combine(
            getLastCheckedFlow(),
            getReleaseDateFlow(),
            getValidUntilFlow(),
        ) { latest, release, valid -> DataVersion(latest, release, valid) }
    }
}