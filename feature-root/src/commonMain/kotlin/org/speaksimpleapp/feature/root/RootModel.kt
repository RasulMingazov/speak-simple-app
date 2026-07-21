package org.speaksimpleapp.feature.root

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.core.common.presentation.BaseModel
import org.speaksimpleapp.feature.auth.domain.entity.SessionState
import org.speaksimpleapp.feature.auth.domain.usecase.ObserveSessionUseCase
import org.speaksimpleapp.feature.auth.domain.usecase.RestoreSessionUseCase

internal class RootModel(
    observeSessionUseCase: ObserveSessionUseCase,
    private val restoreSessionUseCase: RestoreSessionUseCase,
    private val onDestinationChanged: (Destination) -> Unit,
    coroutineDispatchers: CoroutineDispatchers,
) : BaseModel(coroutineDispatchers) {

    private val dataState = MutableStateFlow(DataState())

    val isBootstrapping: StateFlow<Boolean> = dataState.mapState {
        it.destination == Destination.BOOTSTRAP
    }

    init {
        observeSessionUseCase()
            .onEach(::onSessionStateChanged)
            .launchIn(modelScope)

        modelScope.launch { restoreSessionUseCase() }
    }

    private fun onSessionStateChanged(sessionState: SessionState) {
        val destination = when (sessionState) {
            is SessionState.Initializing -> Destination.BOOTSTRAP
            is SessionState.SignedOut -> Destination.LOGIN
            is SessionState.SignedIn -> Destination.MAIN
        }
        if (destination == dataState.value.destination) return

        dataState.update { it.copy(destination = destination) }
        onDestinationChanged(destination)
    }

    data class DataState(
        val destination: Destination = Destination.BOOTSTRAP,
    )

    enum class Destination {
        BOOTSTRAP,
        LOGIN,
        MAIN,
    }

    class Factory(
        private val observeSessionUseCase: ObserveSessionUseCase,
        private val restoreSessionUseCase: RestoreSessionUseCase,
        private val coroutineDispatchers: CoroutineDispatchers,
    ) {
        operator fun invoke(onDestinationChanged: (Destination) -> Unit) = RootModel(
            observeSessionUseCase = observeSessionUseCase,
            restoreSessionUseCase = restoreSessionUseCase,
            onDestinationChanged = onDestinationChanged,
            coroutineDispatchers = coroutineDispatchers,
        )
    }
}
