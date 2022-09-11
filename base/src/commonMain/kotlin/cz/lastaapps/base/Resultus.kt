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

@file:Suppress("NOTHING_TO_INLINE")

package cz.lastaapps.base

import cz.lastaapps.base.errors.ErrorResult

// kinda sus
sealed interface Resultus<out T : Any?> {
    data class Success<out T : Any?>(val data: T) : Resultus<T>
    data class Error(val error: ErrorResult) : Resultus<Nothing>
}

inline fun <T : Any?> Resultus<T>.getOrElse(onError: (error: ErrorResult) -> Nothing): T =
    when (this) {
        is Resultus.Success -> data
        is Resultus.Error -> onError(error)
    }

inline fun <R : Any?> R.toResultus() = Resultus.Success(this)
inline fun ErrorResult.toResultus() = Resultus.Error(this)

@Suppress("unused")
fun ErrorResult?.toResultus(): Nothing = error("Not supported call")

val unitResultus = Unit.toResultus()
