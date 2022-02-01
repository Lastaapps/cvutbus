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

/**  Copyright 2020 molikuner
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package cz.lastaapps.database

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.db.use
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.util.*

/**
 * A simple wrapper for the SqlDriver defined for the JVM, which automatically
 * upgrades and initializes the DB.
 * **You should not just replace the JdbcSqliteDriver with this class in a
 * running system, but it's useful on new systems.**
 *
 * @param schema your schema for the DB
 * @param path your path to the DB file/[JvmSqliteDriver.IN_MEMORY] for in memory DBs
 * @param properties your properties for the underlying JdbcSqliteDriver
 */

// Stolen from https://github.com/molikuner/sqldelight-simple-jvm-driver/
class JvmSqliteDriver @JvmOverloads constructor(
    schema: SqlDriver.Schema,
    path: String,
    properties: Properties = Properties(),
) : SqlDriver by JdbcSqliteDriver(path.normalizedDBPath, properties) {

    init {
        val initSchemaVersion = databaseSchemaVersion()
        when {
            initSchemaVersion == 0 -> {
                schema.create(this)
                setDatabaseSchemaVersion(schema.version)
            }
            initSchemaVersion < schema.version -> {
                schema.migrate(this, initSchemaVersion, schema.version)
                setDatabaseSchemaVersion(schema.version)
            }
            initSchemaVersion > schema.version -> throw IllegalStateException("You can't downgrade?")
        }
    }

    /**
     * Return the current database schema version. Useful when migrating,
     * but should always be the newest version. Recreate this driver to migrate.
     *
     * @return the current schema version
     */
    private fun databaseSchemaVersion(): Int = executeQuery(null, "PRAGMA user_version", 0).use {
        it.getLong(0)?.toInt()
            ?: throw IllegalStateException("Could not get schema version from db")
    }

    private fun setDatabaseSchemaVersion(newVersion: Int) {
        execute(null, "PRAGMA user_version = $newVersion", 0)
    }

    companion object {
        /**
         * A simple string to create a in memory DB. Pass as path parameter in [JvmSqliteDriver] constructor.
         */
        const val IN_MEMORY: String = ""

        private val String.normalizedDBPath: String
            get() = "jdbc:sqlite:${
                "^(?:jdbc:)?(?:sqlite:)?(.+)$".toRegex().matchEntire(this)?.groupValues?.get(1)
                    ?: throw IllegalArgumentException("Could not normalize database path")
            }"
    }
}
