package org.speaksimpleapp.feature.root

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.core.common.presentation.BaseModel
import org.speaksimpleapp.feature.auth.domain.entity.SessionState
import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository
import org.speaksimpleapp.feature.auth.domain.usecase.RestoreSessionUseCase

internal class RootModel(
    authRepository: AuthRepository,
    restoreSession: RestoreSessionUseCase,
    coroutineDispatchers: CoroutineDispatchers,
) : BaseModel(coroutineDispatchers) {
    val uiState: StateFlow<RootComponent.UiState> = authRepository.sessionState.mapState { state ->
        when (state) {
            SessionState.Initializing -> RootComponent.UiState.INITIALIZING
            SessionState.SignedOut -> RootComponent.UiState.LOGIN
            is SessionState.SignedIn -> RootComponent.UiState.CHAT
        }
    }

    init {
        modelScope.launch { restoreSession() }
    }

    class Factory(
        private val authRepository: AuthRepository,
        private val restoreSession: RestoreSessionUseCase,
        private val coroutineDispatchers: CoroutineDispatchers,
    ) {
        operator fun invoke() = RootModel(authRepository, restoreSession, coroutineDispatchers)
    }
}
