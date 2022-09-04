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

package cz.lastaapps.cvutbus.api.worker

import cz.lastaapps.cvutbus.api.DatabaseInfo
import cz.lastaapps.cvutbus.api.UnsupportedConfigVersion
import cz.lastaapps.database.util.CET
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.first
import kotlinx.datetime.*
import java.io.File

suspend fun UpdateWorker.checkUpdateRequired(): Boolean {
    val info = store.databaseInfo.first() ?: return true
    val now: LocalDate = Clock.System.now().toLocalDateTime(CET).date

    return info.dataValidUntil.minus(
        UpdateWorker.dataInvalidDaysBeforeInvalidity,
        DateTimeUnit.DAY
    ) <= now
}

@Throws(UnsupportedConfigVersion::class)
suspend fun UpdateWorker.fetchConfig(): DatabaseInfo {
    val response = client.get(store.dataSourceConfig.first()) {}

    if (response.status.value == 200) {
        val json = response.bodyAsText()
        return DatabaseInfo.fromJson(json)
    }
    throw ResponseException(response, response.bodyAsText())
}

suspend fun UpdateWorker.downloadDatabase(): ByteArray {
    val response = client.get(store.dataSourceDatabase.first())

    if (response.status.value == 200) {
        return response.readBytes()
    }
    throw ResponseException(response, response.bodyAsText())
}

@Suppress("BlockingMethodInNonBlockingContext")
fun writeToFile(file: File, array: ByteArray) {
    if (file.exists()) file.delete()
    file.createNewFile()

    file.writeBytes(array)
}

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun UpdateWorker.updateDatabase(databaseName: String) {
//    val newDriver = Pid(applicationContext, databaseName).createDriver()
//    val newDb = createDatabase(newDriver)
//    val appDb = databaseProvider.provideDatabase()
//
//    appDb.transaction {
//        appDb.calendarQueries.deleteAll()
//        appDb.routesQueries.deleteAll()
//        appDb.stopTimesQueries.deleteAll()
//        appDb.stopsQueries.deleteAll()
//        appDb.tripsQueries.deleteAll()
//
//        newDb.calendarQueries.getAll().executeAsList().forEach {
//            appDb.calendarQueries.insertObj(it)
//        }
//        newDb.routesQueries.getAll().executeAsList().forEach {
//            appDb.routesQueries.insertObj(it)
//        }
//        newDb.stopTimesQueries.getAll().executeAsList().forEach {
//            appDb.stopTimesQueries.insertObj(it)
//        }
//        newDb.stopsQueries.getAll().executeAsList().forEach {
//            appDb.stopsQueries.insertObj(it)
//        }
//        newDb.tripsQueries.getAll().executeAsList().forEach {
//            appDb.tripsQueries.insertObj(it)
//        }
//    }
//
//    newDriver.close()
}

@Suppress("SameParameterValue")
fun UpdateWorker.deleteDatabase(name: String) {
    applicationContext.getDatabasePath(name).delete()
}
