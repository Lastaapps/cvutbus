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
import cz.lastaapps.database.DatabaseDriverFactoryImpl
import cz.lastaapps.database.PIDDatabase
import cz.lastaapps.database.createDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseProvider {

    private val app: Application
    private val store: DatabaseInfoStore
    private val dispatcher: CoroutineDispatcher

    constructor(
        app: Application, store: DatabaseInfoStore, dispatcher: CoroutineDispatcher,
    ) {
        this.app = app; this.store = store; this.dispatcher = dispatcher
    }

    @Inject
    constructor(
        app: Application, store: DatabaseInfoStore,
    ) : this(app, store, Dispatchers.IO)

    companion object {
        private const val assetName = "piddatabase.db"
        private const val assetJsonName = "config.json"
        private const val databaseName = "piddatabase.db"
        private const val newDatabaseName = "piddatabase_new.db"
        private const val newJsonName = "config_new.json"
    }

    private val mutex = Mutex()
    private var databaseCache: PIDDatabase? = null

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun provideDatabase(): PIDDatabase = mutex.withLock {
        withContext(dispatcher) {

            databaseCache?.let { return@withContext it }

            val databaseFile = app.getDatabasePath(databaseName)
            val newDatabaseFile = app.getDatabasePath(newDatabaseName)

            if (newDatabaseFile.exists()) {
                val jsonText =
                    app.getFileStreamPath(newJsonName).inputStream().bufferedReader().readText()
                store.setDatabaseInfo(DatabaseInfo.fromJson(jsonText))

                databaseFile.delete()
                newDatabaseFile.renameTo(databaseFile)

            } else if (!databaseFile.exists()) {
                val jsonText = app.assets.open(assetJsonName).bufferedReader().readText()
                store.setDatabaseInfo(DatabaseInfo.fromJson(jsonText))

                val input = app.assets.open(assetName)
                databaseFile.createNewFile()
                databaseFile.writeBytes(input.readBytes())
                input.close()
            }

            return@withContext createDatabase(
                DatabaseDriverFactoryImpl(
                    app,
                    databaseName
                )
            ).also { databaseCache = it }
        }
    }
}