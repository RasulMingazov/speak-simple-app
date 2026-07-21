package org.speaksimpleapp.feature.auth.domain.usecase

import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository

internal interface DeleteAccountUseCase {
    suspend operator fun invoke()
}

internal class DefaultDeleteAccountUseCase(
    private val repository: AuthRepository,
) : DeleteAccountUseCase {
    override suspend fun invoke() = repository.deleteAccount()
}
