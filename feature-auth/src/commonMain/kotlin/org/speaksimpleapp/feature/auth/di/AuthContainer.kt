package org.speaksimpleapp.feature.auth.di

import org.speaksimpleapp.feature.auth.domain.usecase.ObserveSessionUseCase
import org.speaksimpleapp.feature.auth.domain.usecase.RestoreSessionUseCase
import org.speaksimpleapp.feature.auth.presentation.LoginComponent

interface AuthContainer {
    val observeSessionUseCase: ObserveSessionUseCase
    val restoreSessionUseCase: RestoreSessionUseCase
    val loginComponentFactory: LoginComponent.Factory
}
