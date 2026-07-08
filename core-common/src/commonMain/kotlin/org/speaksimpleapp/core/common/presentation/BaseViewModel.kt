package org.speaksimpleapp.core.common.presentation

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers

abstract class BaseViewModel(
    coroutineDispatchers: CoroutineDispatchers
) : InstanceKeeper.Instance {

    protected val viewModelScope: CoroutineScope = CoroutineScope(
        coroutineDispatchers.main + SupervisorJob()
    )

    final override fun onDestroy() {
        onCleared()
        viewModelScope.cancel()
    }

    protected fun <T, R> StateFlow<T>.mapState(
        transform: (T) -> R
    ): StateFlow<R> =
        map(transform).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = transform(value)
        )

    protected open fun onCleared() = Unit
}
