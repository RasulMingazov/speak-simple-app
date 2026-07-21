package org.speaksimpleapp.feature.auth.di

import org.speaksimpleapp.core.common.coroutines.DefaultCoroutineDispatchers
import org.speaksimpleapp.feature.auth.data.local.DefaultAuthSessionLocalDataSource
import org.speaksimpleapp.feature.auth.data.remote.KtorAuthRemoteDataSource
import org.speaksimpleapp.feature.auth.data.remote.platformHttpClientEngine
import org.speaksimpleapp.feature.auth.data.repository.DefaultAuthRepository
import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository
import org.speaksimpleapp.feature.auth.domain.usecase.DefaultLoginWithGoogleUseCase
import org.speaksimpleapp.feature.auth.domain.usecase.DefaultObserveSessionUseCase
import org.speaksimpleapp.feature.auth.domain.usecase.DefaultRestoreSessionUseCase
import org.speaksimpleapp.feature.auth.domain.usecase.ObserveSessionUseCase
import org.speaksimpleapp.feature.auth.domain.usecase.RestoreSessionUseCase
import org.speaksimpleapp.feature.auth.presentation.DefaultLoginComponent
import org.speaksimpleapp.feature.auth.presentation.LoginComponent
import org.speaksimpleapp.feature.auth.presentation.LoginModel

internal class DefaultAuthContainer(
    config: AuthRuntimeConfig,
    platform: AuthPlatformDependencies,
) : AuthContainer {

    private val remoteDataSource = KtorAuthRemoteDataSource(
        baseUrl = config.apiBaseUrl,
        engine = platformHttpClientEngine(),
    )
    private val localDataSource = DefaultAuthSessionLocalDataSource(
        storage = platform.secureSessionStorage,
    )

    private val authRepository: AuthRepository = DefaultAuthRepository(
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource,
        googleIdentityProvider = platform.googleIdentityProvider,
        devicePlatform = config.devicePlatform,
    )

    override val observeSessionUseCase: ObserveSessionUseCase =
        DefaultObserveSessionUseCase(authRepository)

    override val restoreSessionUseCase: RestoreSessionUseCase =
        DefaultRestoreSessionUseCase(authRepository)

    override val loginComponentFactory: LoginComponent.Factory = DefaultLoginComponent.Factory(
        LoginModel.Factory(
            loginWithGoogleUseCase = DefaultLoginWithGoogleUseCase(authRepository),
            coroutineDispatchers = DefaultCoroutineDispatchers,
        ),
    )
}
