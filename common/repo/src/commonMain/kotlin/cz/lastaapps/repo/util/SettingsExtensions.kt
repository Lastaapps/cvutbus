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

@file:OptIn(ExperimentalSettingsApi::class)

package cz.lastaapps.repo.util

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

suspend fun <T : Any> FlowSettings.getSerializable(
    deserializer: DeserializationStrategy<T>, key: String, default: T
): T = getSerializableOrNull(deserializer, key) ?: default


suspend fun <T : Any> FlowSettings.getSerializableOrNull(
    deserializer: DeserializationStrategy<T>, key: String,
): T? = getStringOrNull(key)?.let { Json.decodeFromString(deserializer, it) }


fun <T : Any> FlowSettings.getSerializableFlow(
    deserializer: DeserializationStrategy<T>, key: String, default: T,
): Flow<T> = getSerializableOrNullFlow(deserializer, key).map { it ?: default }


fun <T : Any> FlowSettings.getSerializableOrNullFlow(
    deserializer: DeserializationStrategy<T>, key: String,
): Flow<T?> = getStringOrNullFlow(key).map { it?.let { Json.decodeFromString(deserializer, it) } }

suspend fun <T : Any> FlowSettings.putSerializable(
    encoder: SerializationStrategy<T>,
    key: String,
    value: T?
) =
    if (value == null)
        remove(key)
    else putString(key, Json.encodeToString(encoder, value))
