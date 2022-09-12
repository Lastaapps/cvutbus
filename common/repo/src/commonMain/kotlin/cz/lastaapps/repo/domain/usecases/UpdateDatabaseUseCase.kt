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

import cz.lastaapps.base.Resultus
import cz.lastaapps.base.usecase.UseCaseParam
import cz.lastaapps.base.usecase.UseCaseResultus
import cz.lastaapps.base.usecase.UseCaseResultusImpl
import cz.lastaapps.repo.domain.UpdateRepository

interface UpdateDatabaseUseCase : UseCaseResultus<UpdateDatabaseUseCase.Params, Unit> {
    data class Params(val force: Boolean) : UseCaseParam
}

internal class UpdateDatabaseUseCaseImpl(
    private val repo: UpdateRepository,
) : UpdateDatabaseUseCase, UseCaseResultusImpl<UpdateDatabaseUseCase.Params, Unit>() {
    override suspend fun doWork(params: UpdateDatabaseUseCase.Params): Resultus<Unit> =
        repo.updateFromRepo(params.force)
}
