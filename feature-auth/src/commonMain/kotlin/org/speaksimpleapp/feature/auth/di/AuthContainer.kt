package org.speaksimpleapp.feature.auth.di

import org.speaksimpleapp.feature.auth.data.identity.GoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.local.SecureSessionStorage
import org.speaksimpleapp.feature.auth.data.platform.entity.DevicePlatform
import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository
import org.speaksimpleapp.feature.auth.domain.usecase.DeleteAccountUseCase
import org.speaksimpleapp.feature.auth.domain.usecase.LogoutUseCase
import org.speaksimpleapp.feature.auth.domain.usecase.RestoreSessionUseCase
import org.speaksimpleapp.feature.auth.presentation.LoginComponent

data class AuthRuntimeConfig(
    val apiBaseUrl: String,
    val devicePlatform: DevicePlatform,
)

interface AuthContainer {
    val authRepository: AuthRepository
    val restoreSession: RestoreSessionUseCase
    val logout: LogoutUseCase
    val deleteAccount: DeleteAccountUseCase
    val loginComponentFactory: LoginComponent.Factory
}

interface AuthPlatformDependencies {
    val googleIdentityProvider: GoogleIdentityProvider
    val secureSessionStorage: SecureSessionStorage
}
