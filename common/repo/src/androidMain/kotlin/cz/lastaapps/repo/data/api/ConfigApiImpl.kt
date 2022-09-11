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

package cz.lastaapps.repo.data.api

import android.content.Context
import cz.lastaapps.base.Resultus
import cz.lastaapps.base.errors.UpdateErrors
import cz.lastaapps.base.toResultus
import cz.lastaapps.base.utils.runCatchingKtor
import cz.lastaapps.database.AndroidDatabaseDriverFactoryImpl
import cz.lastaapps.database.createUpdateDatabaseSource
import cz.lastaapps.database.domain.UpdateDataSource
import cz.lastaapps.repo.domain.model.dto.ConfigDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.lighthousegames.logging.logging

internal class ConfigApiImpl(
    private val client: HttpClient,
    private val context: Context,
) : ConfigApi {
    companion object {
        private const val tempFileName = "temp_pid_database.db"
        private val log = logging()
    }

    override suspend fun downloadConfig(url: String): Resultus<ConfigDto> = runCatchingKtor {
        client.get(url).body<ConfigDto>().toResultus()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun downloadDatabase(src: String): Resultus<UpdateDataSource> =
        runCatchingKtor {
            val bytes = client.get(src).body<ByteArray>()

            try {
                val file = context.getDatabasePath(tempFileName)
                if (!file.exists())
                    file.createNewFile()
                file.writeBytes(bytes)

                createUpdateDatabaseSource(
                    AndroidDatabaseDriverFactoryImpl(
                        context,
                        tempFileName
                    ).createDriver()
                ).toResultus()
            } catch (e: Exception) {
                log.e(e) { "Failed to write database file" }
                UpdateErrors.FailedToSaveOrLoadDatabase(e).toResultus()
            }
        }

    override suspend fun deleteDatabase() {
        try {
            val file = context.getDatabasePath(tempFileName)
            if (!file.exists())
                file.delete()
        } catch (e: Exception) {
            log.e(e) { "Failed to delete database file" }
        }
    }
}