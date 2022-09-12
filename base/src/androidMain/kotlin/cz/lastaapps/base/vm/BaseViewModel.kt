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

package cz.lastaapps.base.vm

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

abstract class BaseIntentViewModel<S : VMState, I : VMIntent>(
    initial: S,
    private val dispatcher: CoroutineContext = Dispatchers.Default,
) : ViewModel() {

    private val mStateFlow = MutableStateFlow(initial)
    protected val state get() = mStateFlow.value

    private val lifecycleMutex = Mutex()
    val flow = mStateFlow.run {
        onSubscription { lifecycleMutex.withLock { onResume() } }
            .onCompletion { lifecycleMutex.withLock { onPause() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(4_000), value)
    }

    operator fun invoke(intent: I) {
        viewModelScope.launch(dispatcher) {
            intent.hashCode()
        }
    }

    protected open suspend fun onResume() {}
    protected open suspend fun onPause() {}

    protected abstract suspend fun I.handleIntent()

    protected fun update(block: S.() -> S) = mStateFlow.update { it.run(block) }
}

interface VMIntent
interface VMState

@OptIn(ExperimentalLifecycleComposeApi::class)
val <S : VMState, I : VMIntent> BaseIntentViewModel<S, I>.state
    @Composable
    get() = flow.collectAsStateWithLifecycle()
