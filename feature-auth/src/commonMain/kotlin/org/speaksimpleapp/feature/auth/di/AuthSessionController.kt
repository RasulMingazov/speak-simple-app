package org.speaksimpleapp.feature.auth.di

import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.feature.auth.domain.entity.SessionState
import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository
import org.speaksimpleapp.feature.auth.domain.usecase.RestoreSessionUseCase

interface AuthSessionController {
    val state: StateFlow<SessionState>

    suspend fun restore()
}

internal class DefaultAuthSessionController(
    repository: AuthRepository,
    private val restoreSessionUseCase: RestoreSessionUseCase,
) : AuthSessionController {
    override val state: StateFlow<SessionState> = repository.sessionState

    override suspend fun restore() = restoreSessionUseCase()
}
