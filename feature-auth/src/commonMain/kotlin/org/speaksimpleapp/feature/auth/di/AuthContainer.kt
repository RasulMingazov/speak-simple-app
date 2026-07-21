package org.speaksimpleapp.feature.auth.di

import org.speaksimpleapp.feature.auth.presentation.LoginComponent

interface AuthContainer {
    val sessionController: AuthSessionController
    val loginComponentFactory: LoginComponent.Factory
}
