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

package cz.lastaapps.repo.domain

import cz.lastaapps.database.domain.model.DepartureInfo
import cz.lastaapps.database.domain.model.TransportConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

internal interface PIDRepo {
    suspend fun getData(
        from: Instant,
        connection: TransportConnection,
    ): Flow<List<DepartureInfo>>

    suspend fun getLatestData(
        connection: TransportConnection,
        nowProvider: () -> Instant = { Clock.System.now() },
        includePast: Duration = 1.minutes,
    ): Flow<List<DepartureInfo>>
}