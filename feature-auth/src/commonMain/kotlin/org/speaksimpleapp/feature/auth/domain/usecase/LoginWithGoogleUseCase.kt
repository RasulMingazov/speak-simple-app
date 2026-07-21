package org.speaksimpleapp.feature.auth.domain.usecase

import org.speaksimpleapp.feature.auth.domain.entity.LoginResult
import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository

interface LoginWithGoogleUseCase {
    suspend operator fun invoke(): LoginResult
}

internal class DefaultLoginWithGoogleUseCase(
    private val repository: AuthRepository,
) : LoginWithGoogleUseCase {
    override suspend fun invoke(): LoginResult = repository.loginWithGoogle()
}
