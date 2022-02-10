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

package cz.lastaapps.generator

import cz.lastaapps.database.DatabaseDriverFactoryImpl
import cz.lastaapps.database.MemoryDriverFactory
import cz.lastaapps.database.PIDDatabase
import cz.lastaapps.database.createDatabase
import cz.lastaapps.entity.StopName
import cz.lastaapps.entity.utils.CET
import cz.lastaapps.generator.parsers.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaZoneId
import org.lighthousegames.logging.logging
import java.io.*
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.system.exitProcess

val genLog = logging("General")
val downLog = logging("Download")
val zipLog = logging("Unzipping")
val memLog = logging("Memory db")
val parseLog = logging("Parsing")
val dbLog = logging("Database")
val jsonLog = logging("Json")
val cleanLog = logging("Clean up")

const val dirPath = "pid_data"
const val archiveName = "PID_GTFS.zip"
const val databaseName = "piddatabase.db"
const val jsonName = "config.json"

/**
 * Filters all the non-important trips form the dara provided by PID,
 * so a small database can be uploaded to the net
 */
fun main(): Unit = runBlocking {

    val skipDownload = false
    val skipZip = false
    val skipCleanup = false

    val dir = File(dirPath)
    dir.mkdirs()
    dir.mkdir()

    genLog.i { "Working directory: " + System.getProperty("user.dir") }
    genLog.i { "Output directory: " + dir.absolutePath }

    val archive = File(dir, archiveName)
    if ((!archive.exists() || Instant.ofEpochMilli(archive.lastModified() + 24 * 3600 * 1000) < Instant.now()) && !skipDownload) {
        downLog.i { "Downloading from http://data.pid.cz/PID_GTFS.zip" }

        if (!archive.exists())
            archive.createNewFile()

        val succeeded = HttpClient(CIO).downloadFile(archive, "http://data.pid.cz/PID_GTFS.zip") {
            //print("\rDownload progress: ${it * 100} %")
        }
        if (!succeeded) {
            downLog.i { "Download failed!" }
            try {
                archive.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            exitProcess(1)
        }
        downLog.i { "Downloaded" }
    } else {
        downLog.i { "Download skipped" }
    }

    zipLog.i { "Unzipping..." }
    if (!skipZip)
        unzip(archive, dir)
    zipLog.i { "Unzipped!" }


    val completeDatabase = createDatabase(MemoryDriverFactory())
    memLog.i { "Filling temp database" }
    loadData(dir, completeDatabase)
    memLog.i { "Loading done" }

    val stationPairs = listOf(
        "Koleje Strahov" to "Dejvická",
    ).map { StopName(it.first) to StopName(it.second) }

    val databaseFile = File(dir, databaseName)
    if (databaseFile.exists()) databaseFile.delete()
    databaseFile.createNewFile()

    val database = createDatabase(DatabaseDriverFactoryImpl(databaseFile))
    stationPairs.forEach {
        dbLog.i { "Querying data for ${it.first} - ${it.second}" }
        completeDatabase.queriesQueries.getAllColumns(it.first, it.second).executeAsList()
            .also { list ->
                dbLog.i { "Connections found: ${list.size}" }
            }.forEach { row ->
                database.stopsQueries.insert(row.stop_id, row.stop_name)
                database.stopsQueries.insert(row.stop_id_, row.stop_name_)

                database.routesQueries.insert(
                    row.route_id, row.route_short_name, row.route_long_name,
                )

                database.tripsQueries.insert(
                    row.trip_id, row.route_id, row.service_id, row.trip_headsign,
                )

                database.stopTimesQueries.insert(
                    row.stop_id__, row.trip_id, row.arrival_time, row.departure_time,
                )
                database.stopTimesQueries.insert(
                    row.stop_id___, row.trip_id_, row.arrival_time_, row.departure_time_,
                )

                database.calendarQueries.insert(
                    row.service_id, row.days, row.start_date, row.end_date,
                )
            }
    }
    dbLog.i { "Data saved" }

    jsonLog.i { "Creating json configuration" }
    val json = File(dir, jsonName)
    val jsonOut = PrintWriter(OutputStreamWriter(json.outputStream()))
    val now = LocalDate.now(CET.toJavaZoneId())
    jsonOut.print(
        """
        |{
        |   "jsonVersion": 1,
        |   "minAppVersion": 1,
        |   "dataReleaseDate": "${now.format(DateTimeFormatter.ISO_DATE)}",
        |   "dataValidity": "${now.plusDays(10).format(DateTimeFormatter.ISO_DATE)}",
        |   "fileSize": ${databaseFile.length()}
        |}
    """.trimMargin()
    )
    jsonOut.flush()
    jsonOut.close()

    cleanLog.i { "Cleaning up..." }
    if (!skipCleanup)
        cleanup(dir)

    genLog.i { "Done, bye" }
}

@OptIn(ExperimentalIoApi::class)
internal suspend fun HttpClient.downloadFile(
    file: File,
    url: String,
    onProgress: (Float) -> Unit
): Boolean {
    val response = request<HttpResponse> {
        url(url)
        method = HttpMethod.Get
    }

    val data = ByteArray(response.contentLength()!!.toInt())
    var offset = 0

    do {
        //TODO download progress
        val currentRead = response.content.readAvailable(data, offset, data.size)
        offset += currentRead
        onProgress(1f * offset / data.size)
    } while (currentRead > 0)

    if (!response.status.isSuccess()) {
        return false
    }
    file.writeBytes(data)
    return true
}

internal fun unzip(archive: File, dir: File) {
    val buffer = ByteArray(1024)
    val zis = ZipInputStream(FileInputStream(archive))
    var zipEntry = zis.nextEntry
    while (zipEntry != null) {
        zipLog.i { "Unzipping ${zipEntry.name}" }
        val newFile = newFile(dir, zipEntry)
        if (zipEntry.isDirectory) {
            if (!newFile.isDirectory && !newFile.mkdirs()) {
                throw IOException("Failed to create directory $newFile")
            }
        } else {
            // fix for Windows-created archives
            val parent = newFile.parentFile!!
            if (!parent.isDirectory && !parent.mkdirs()) {
                throw IOException("Failed to create directory $parent")
            }

            // write file content
            val fos = FileOutputStream(newFile)
            var len: Int
            while (zis.read(buffer).also { len = it } > 0) {
                fos.write(buffer, 0, len)
            }
            fos.close()
        }
        zipEntry = zis.nextEntry
    }
    zis.closeEntry()
    zis.close()
}

internal fun newFile(dest: File, zipEntry: ZipEntry): File {
    val destFile = File(dest, zipEntry.name)

    val destDirPath = dest.canonicalPath
    val destFilePath = destFile.canonicalPath

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
        throw IOException("Entry is outside of the target dir: " + zipEntry.name)
    }

    return destFile
}

internal fun cleanup(dest: File) {
    dest.listFiles()!!.forEach {
        if (it.name.endsWith(".txt"))
            it.delete()
    }
}

internal fun loadData(dir: File, database: PIDDatabase) {

    fun stream(name: String): InputStream = File(dir, "$name.txt").inputStream()

    database.transaction {
        parseLog.i { "Parsing calendar.txt" }
        CalendarParser.parse(stream("calendar")).forEach {
            database.calendarQueries.insert(it.serviceId, it.days, it.start, it.end)
        }
        parseLog.i { "Parsing routes.txt" }
        RoutesParser.parse(stream("routes")).forEach {
            database.routesQueries.insert(it.routeId, it.shortName, it.longName)
        }
        parseLog.i { "Parsing stops.txt" }
        StopsParser.parse(stream("stops")).forEach {
            database.stopsQueries.insert(it.stopId, it.name)
        }
        parseLog.i { "Parsing stop_times.txt" }
        StopTimesParser.parse(stream("stop_times")).forEach {
            database.stopTimesQueries.insert(it.stopId, it.tripId, it.arrival, it.departure)
        }
        parseLog.i { "Parsing trips.txt" }
        TripsParser.parse(stream("trips")).forEach {
            database.tripsQueries.insert(
                it.tripId, it.routeId, it.serviceId, it.headSign,
            )
        }
    }
}


