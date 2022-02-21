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

package cz.lastaapps.cvutbus.notification.receivers

/*
import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import cz.lastaapps.cvutbus.di.DependencyGraphInitializer
import cz.lastaapps.cvutbus.di.InitializerEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import javax.inject.Inject

@Keep
class StartupInit : Initializer<Unit> {

    @Inject
    lateinit var registerModule: RegisterModule

    override fun create(context: Context) {
        logging().i { "Creating" }

        InitializerEntryPoint.resolve(context).inject(this)

        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            registerModule.update()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf(DependencyGraphInitializer::class.java)
}
 */