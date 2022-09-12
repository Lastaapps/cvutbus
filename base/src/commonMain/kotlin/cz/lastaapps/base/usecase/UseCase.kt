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

package cz.lastaapps.base.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface UseCase<P : UseCaseParam, R : Any?> {
    suspend operator fun invoke(params: P): R
}

abstract class UseCaseImpl<P : UseCaseParam, R : Any?>(private val dispatcher: CoroutineContext = Dispatchers.Default) :
    UseCase<P, R> {
    override suspend fun invoke(params: P): R = withContext(dispatcher) { doWork(params) }

    protected abstract suspend fun doWork(params: P): R
}
