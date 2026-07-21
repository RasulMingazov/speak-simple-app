package org.speaksimpleapp.feature.root

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.core.common.presentation.BaseModel
import org.speaksimpleapp.feature.auth.di.AuthSessionController
import org.speaksimpleapp.feature.auth.domain.entity.SessionState

internal class RootModel(
    private val authSessionController: AuthSessionController,
    private val onDestinationChanged: (Destination) -> Unit,
    coroutineDispatchers: CoroutineDispatchers,
) : BaseModel(coroutineDispatchers) {

    private val dataState = MutableStateFlow(DataState())

    val isBootstrapping: StateFlow<Boolean> = dataState.mapState {
        it.destination == Destination.BOOTSTRAP
    }

    init {
        authSessionController.state
            .onEach(::onSessionStateChanged)
            .launchIn(modelScope)

        modelScope.launch { authSessionController.restore() }
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
        private val authSessionController: AuthSessionController,
        private val coroutineDispatchers: CoroutineDispatchers,
    ) {
        operator fun invoke(onDestinationChanged: (Destination) -> Unit) = RootModel(
            authSessionController = authSessionController,
            onDestinationChanged = onDestinationChanged,
            coroutineDispatchers = coroutineDispatchers,
        )
    }
}
