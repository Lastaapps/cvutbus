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

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.json.JSONObject
import java.time.format.DateTimeFormatter


data class DatabaseInfo(
    val minAppVersion: Long,
    val dataReleaseDate: LocalDate,
    val dataValidUntil: LocalDate,
    val fileSize: Long,
) {
    companion object {
        private const val supportedJsonVersion = 1L

        fun fromJson(jsonString: String): DatabaseInfo {
            val json = JSONObject(jsonString)

            json.getLong("jsonVersion").takeIf { it != supportedJsonVersion }?.let {
                throw UnsupportedConfigVersion(it)
            }

            return DatabaseInfo(
                json.getLong("minAppVersion"),
                json.getString("dataReleaseDate").toDate(),
                json.getString("dataValidity").toDate(),
                json.getLong("fileSize"),
            )
        }

        private fun String.toDate(): LocalDate {
            return java.time.LocalDate.parse(this, DateTimeFormatter.ISO_DATE).toKotlinLocalDate()
        }
    }

}