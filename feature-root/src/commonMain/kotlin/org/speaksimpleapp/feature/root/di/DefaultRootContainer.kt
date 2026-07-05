package org.speaksimpleapp.feature.root.di

import org.speaksimpleapp.feature.chat.di.DefaultChatContainer
import org.speaksimpleapp.feature.root.DefaultRootComponent
import org.speaksimpleapp.feature.root.RootComponent

class DefaultRootContainer : RootContainer {

    private val chatContainer: DefaultChatContainer by lazy {
        DefaultChatContainer()
    }

    override val rootComponentFactory: RootComponent.Factory by lazy {
        DefaultRootComponent.Factory(
            chatComponentFactory = chatContainer.chatComponentFactory
        )
    }
}
