/*
 * Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 * This file is part of ČVUT Bus.
 *
 * Menza is free software: you can redistribute it and/or modify
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

package cz.lastaapps.database.testdata

import java.io.File
import java.io.InputStream

object ResourceOpener {
    fun openResource(name: String): InputStream {

        //TODO fix resources reading
//        javaClass.getResource("trips.txt").shouldBeNull()
//        javaClass.getResource("/trips.txt").shouldBeNull()
//        javaClass.getResource("cz/lastaapps/database/testdata/trips.txt").shouldBeNull()
//        javaClass.getResource("/cz/lastaapps/database/testdata/trips.txt").shouldBeNull()
//        javaClass.getResource("testdata/trips.txt").shouldBeNull()
//        javaClass.getResource("/testdata/trips.txt").shouldBeNull()

        //error(System.getProperty("user.dir")) // ./storage/database/
        return File("./src/desktopTest/kotlin/cz/lastaapps/database/testdata/$name.txt").inputStream()
    }
}