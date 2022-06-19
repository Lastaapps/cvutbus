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

package cz.lastaapps.cvutbus.init

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import org.lighthousegames.logging.logging

class AfterUpdateReceiver : BroadcastReceiver() {

    companion object {
        private val log = logging()
    }

    override fun onReceive(context: Context, intent: Intent) {
        val di by closestDI(context)

        log.i { "Received" }
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED &&
            intent.data.toString() == "package:${context.packageName}"
        ) {
            log.i { "Accepted" }
            val init by di.instance<RunInit>()
            init.run()
        }
    }
}
