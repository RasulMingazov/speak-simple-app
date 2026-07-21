package org.speaksimpleapp.feature.auth.domain.usecase

import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository

internal interface LogoutUseCase {
    suspend operator fun invoke()
}

internal class DefaultLogoutUseCase(
    private val repository: AuthRepository,
) : LogoutUseCase {
    override suspend fun invoke() = repository.logout()
}
