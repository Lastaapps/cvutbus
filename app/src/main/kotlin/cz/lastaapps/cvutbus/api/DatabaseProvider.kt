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
import cz.lastaapps.database.PIDDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging

class DatabaseProvider(
    @Suppress("JoinDeclarationAndAssignment")
    private val app: Application,
    private val store: DatabaseStore,
    private val dispatcher: CoroutineDispatcher
) {

    companion object {
        private const val assetName = "piddatabase.db"
        private const val assetJsonName = "config.json"
        private const val databaseName = "piddatabase.db"

        private val log = logging()
    }

    private val mutex = Mutex()
    private var databaseCache: PIDDatabase? = null

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun provideDatabase(): PIDDatabase = mutex.withLock {
        withContext(dispatcher) {

            databaseCache?.let { return@withContext it }

            val databaseFile = app.getDatabasePath(databaseName)

//            if (BuildConfig.DEBUG && databaseFile.exists()) {
//                databaseFile.delete()
//            }

            if (!databaseFile.exists()) {
                log.i { "There is no database, loading from assets" }

                val jsonText = app.assets.open(assetJsonName).bufferedReader().readText()
                store.setDatabaseInfo(DatabaseInfo.fromJson(jsonText))
                store.setLastUpdated()

                val input = app.assets.open(assetName)
                databaseFile.createNewFile()
                databaseFile.writeBytes(input.readBytes())
                input.close()
            } else {
                log.i { "Database is up to date" }
            }

            Any() as PIDDatabase
//            return@withContext createDatabase(
//                PIDDatabaseDriverFactory(app).createDriver()
//            ).also { databaseCache = it }
        }
    }
}