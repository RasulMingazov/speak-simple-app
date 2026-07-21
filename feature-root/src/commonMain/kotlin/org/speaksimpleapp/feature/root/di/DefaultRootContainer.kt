package org.speaksimpleapp.feature.root.di

import org.speaksimpleapp.core.common.coroutines.DefaultCoroutineDispatchers
import org.speaksimpleapp.feature.auth.di.AuthContainer
import org.speaksimpleapp.feature.main.di.createMainComponentFactory
import org.speaksimpleapp.feature.main.presentation.MainComponent
import org.speaksimpleapp.feature.root.DefaultRootComponent
import org.speaksimpleapp.feature.root.RootComponent
import org.speaksimpleapp.feature.root.RootModel

internal class DefaultRootContainer(
    authContainer: AuthContainer,
) {
    private val mainComponentFactory: MainComponent.Factory = createMainComponentFactory()

    val rootComponentFactory: RootComponent.Factory =
        DefaultRootComponent.Factory(
            modelFactory = RootModel.Factory(
                authSessionController = authContainer.sessionController,
                coroutineDispatchers = DefaultCoroutineDispatchers,
            ),
            loginComponentFactory = authContainer.loginComponentFactory,
            mainComponentFactory = mainComponentFactory,
        )
}
