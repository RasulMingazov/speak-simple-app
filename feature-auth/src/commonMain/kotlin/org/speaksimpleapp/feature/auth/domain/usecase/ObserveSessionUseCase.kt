package org.speaksimpleapp.feature.auth.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.feature.auth.domain.entity.SessionState
import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository

interface ObserveSessionUseCase {
    operator fun invoke(): StateFlow<SessionState>
}

internal class DefaultObserveSessionUseCase(
    private val repository: AuthRepository,
) : ObserveSessionUseCase {
    override fun invoke(): StateFlow<SessionState> = repository.sessionState
}
