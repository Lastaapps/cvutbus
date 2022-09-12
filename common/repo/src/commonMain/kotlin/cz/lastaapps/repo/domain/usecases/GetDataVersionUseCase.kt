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

package cz.lastaapps.repo.domain.usecases

import cz.lastaapps.base.usecase.UseCaseNoParams
import cz.lastaapps.base.usecase.UseCaseNoParamsImpl
import cz.lastaapps.repo.domain.UpdateRepository
import cz.lastaapps.repo.domain.model.DataVersion
import kotlinx.coroutines.flow.Flow

interface GetDataVersionUseCase : UseCaseNoParams<Flow<DataVersion>>

internal class GetDataVersionUseCaseImpl(
    private val repo: UpdateRepository,
) : GetDataVersionUseCase, UseCaseNoParamsImpl<Flow<DataVersion>>() {
    override suspend fun doWork(): Flow<DataVersion> = repo.getConfig()
}
