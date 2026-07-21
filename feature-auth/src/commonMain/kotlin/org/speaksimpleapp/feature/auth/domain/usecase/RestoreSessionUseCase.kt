package org.speaksimpleapp.feature.auth.domain.usecase

import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository

internal interface RestoreSessionUseCase {
    suspend operator fun invoke()
}

internal class DefaultRestoreSessionUseCase(
    private val repository: AuthRepository,
) : RestoreSessionUseCase {
    override suspend fun invoke() = repository.restoreSession()
}
