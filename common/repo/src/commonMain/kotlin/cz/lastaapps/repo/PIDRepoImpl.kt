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

package cz.lastaapps.repo

import cz.lastaapps.database.domain.PIDDataSource
import cz.lastaapps.database.domain.model.DepartureInfo
import cz.lastaapps.database.domain.model.TransportConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import org.lighthousegames.logging.logging

class PIDRepoImpl(private val pidDataSource: PIDDataSource) : PIDRepo {

    companion object {
        private val log = logging()
    }

    override suspend fun getData(
        fromDateTime: LocalDateTime,
        connection: TransportConnection
    ): Flow<List<DepartureInfo>> {
        return pidDataSource.getData(fromDateTime, connection)
    }
}

fun List<DepartureInfo>.dropOld(limit: LocalDateTime): List<DepartureInfo> {
    if (isEmpty() || first().dateTime >= limit) return this
    return dropWhile { it.dateTime < limit }
}
