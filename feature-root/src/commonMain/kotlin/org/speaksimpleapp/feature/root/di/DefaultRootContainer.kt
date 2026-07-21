package org.speaksimpleapp.feature.root.di

import org.speaksimpleapp.core.common.coroutines.DefaultCoroutineDispatchers
import org.speaksimpleapp.feature.auth.di.AuthContainer
import org.speaksimpleapp.feature.chat.di.DefaultChatContainer
import org.speaksimpleapp.feature.root.DefaultRootComponent
import org.speaksimpleapp.feature.root.RootComponent
import org.speaksimpleapp.feature.root.RootModel

class DefaultRootContainer(
    authContainer: AuthContainer,
) : RootContainer {
    private val chatContainer by lazy(::DefaultChatContainer)

    override val rootComponentFactory: RootComponent.Factory by lazy {
        DefaultRootComponent.Factory(
            modelFactory = RootModel.Factory(
                authRepository = authContainer.authRepository,
                restoreSession = authContainer.restoreSession,
                coroutineDispatchers = DefaultCoroutineDispatchers,
            ),
            loginComponentFactory = authContainer.loginComponentFactory,
            chatComponentFactory = chatContainer.chatComponentFactory,
        )
    }
}
