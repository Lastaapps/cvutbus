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

abstract class LruCache<K : Any, V : Any>(
    private val capacity: Int,
) {
    init {
        require(capacity > 0)
    }

    private val backingMap = HashMap<K, V>()
    private val queue = ArrayDeque<K>(capacity + 1)

    fun get(key: K): V {
        val item = backingMap.getOrPut(key) { createItem(key) }
        queue.remove(key)
        queue.add(key)
        if (queue.size > capacity) {
            val toRemove = queue.removeLast()
            backingMap.remove(toRemove)?.let { destroyItem(it) }
        }
        return item
    }

    abstract fun createItem(key: K): V
    open fun destroyItem(value: V) {}
}
