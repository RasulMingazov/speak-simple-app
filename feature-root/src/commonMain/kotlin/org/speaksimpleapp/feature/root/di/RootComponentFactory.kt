package org.speaksimpleapp.feature.root.di

import org.speaksimpleapp.feature.auth.di.AuthContainer
import org.speaksimpleapp.feature.root.RootComponent

fun createRootComponentFactory(
    authContainer: AuthContainer,
): RootComponent.Factory = DefaultRootContainer(authContainer).rootComponentFactory
