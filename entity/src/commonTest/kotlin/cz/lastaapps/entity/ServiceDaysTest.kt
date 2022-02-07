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

package cz.lastaapps.entity


import io.kotest.matchers.shouldBe
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test

class ServiceDaysTest {

    private val validData = listOf(
        false to DayOfWeek.MONDAY,
        true to DayOfWeek.TUESDAY,
        false to DayOfWeek.WEDNESDAY,
        true to DayOfWeek.THURSDAY,
        false to DayOfWeek.FRIDAY,
        true to DayOfWeek.SATURDAY,
        false to DayOfWeek.SUNDAY,
    )
    private val inverseData = validData.map { (!it.first) to it.second }

    @Test
    fun testChecking() {
        val valid = ServiceDays.fromDays(validData.map { it.first })
        val inverse = ServiceDays.fromDays(inverseData.map { it.first })

        validData.forEach {
            valid.hasDay(it.second) shouldBe it.first
        }
        inverseData.forEach {
            inverse.hasDay(it.second) shouldBe it.first
        }

    }

    @Test
    fun testDatabaseStoring() {
        val valid = ServiceDays.fromDays(validData.map { it.first })
        valid shouldBe ServiceDays.fromDatabase(valid.toDatabase())
    }
}