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

package cz.lastaapps.base.utils

import cz.lastaapps.base.Resultus
import cz.lastaapps.base.errors.NetworkErrors
import cz.lastaapps.base.toResultus
import org.lighthousegames.logging.logging

suspend fun <R : Any> runCatchingKtor(block: suspend () -> Resultus<R>) =
    runCatchingNetworkExceptions { block() }

inline fun <R : Any> runCatchingNetworkExceptions(block: () -> Resultus<R>): Resultus<R> =
    runCatching(block).getOrElse { e ->
        when (val name = e::class.simpleName) {
            "UnknownHostException" -> NetworkErrors.NoNetworkConnection(name, e)
            "NoRouteToHostException",
            "IOException",
            "SSLException",
            "SocketException",
            "HttpRequestTimeoutException",
            "ConnectException",
            "SendCountExceedException",
            "SocketTimeoutException" -> NetworkErrors.FailedToConnect(name, e)

            else -> {
                val log = logging("CatchingNetwork")
                log.e(e) { "Exception wasn't handled" }
                throw e
            }
        }.toResultus()
    }
