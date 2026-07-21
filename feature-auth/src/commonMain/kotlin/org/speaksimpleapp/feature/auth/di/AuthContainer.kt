package org.speaksimpleapp.feature.auth.di

import org.speaksimpleapp.feature.auth.data.identity.GoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.local.SecureSessionStorage
import org.speaksimpleapp.feature.auth.data.platform.entity.DevicePlatform
import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository
import org.speaksimpleapp.feature.auth.domain.usecase.RestoreSessionUseCase
import org.speaksimpleapp.feature.auth.presentation.LoginComponent

internal data class AuthRuntimeConfig(
    val apiBaseUrl: String,
    val devicePlatform: DevicePlatform,
)

interface AuthContainer {
    val authRepository: AuthRepository
    val restoreSession: RestoreSessionUseCase
    val loginComponentFactory: LoginComponent.Factory
}

internal interface AuthPlatformDependencies {
    val googleIdentityProvider: GoogleIdentityProvider
    val secureSessionStorage: SecureSessionStorage
}
