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

package cz.lastaapps.cvutbus.ui.providers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.viewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

// Stolen from LocalViewModelOwner
object LocalActivityViewModelOwner {
    private val LocalActivityViewModelOwner =
        compositionLocalOf<ViewModelStoreOwner?> { null }

    val current: ViewModelStoreOwner
        @Composable
        get() = LocalActivityViewModelOwner.current
            ?: ViewTreeViewModelStoreOwner.get(LocalView.current)!!

    infix fun provides(viewModelStoreOwner: ViewModelStoreOwner):
            ProvidedValue<ViewModelStoreOwner?> {
        return LocalActivityViewModelOwner.provides(viewModelStoreOwner)
    }
}

@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified VM : ViewModel> activityViewModel(
    qualifier: Qualifier? = null,
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
    noinline parameters: ParametersDefinition? = null
): Lazy<VM> = viewModel(qualifier, LocalActivityViewModelOwner.current, scope, parameters)

@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified VM : ViewModel> getActivityViewModel(
    qualifier: Qualifier? = null,
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
    noinline parameters: ParametersDefinition? = null
): VM = getViewModel(qualifier, LocalActivityViewModelOwner.current, scope, parameters)
