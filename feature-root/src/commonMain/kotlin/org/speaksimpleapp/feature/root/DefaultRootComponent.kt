package org.speaksimpleapp.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import org.speaksimpleapp.feature.chat.presentation.ChatComponent

class DefaultRootComponent(
    componentContext: ComponentContext,
    chatComponentFactory: ChatComponent.Factory
) : RootComponent, ComponentContext by componentContext {
    override val model: RootModel = RootModel()
    override val chatComponent: ChatComponent = chatComponentFactory(
        componentContext = childContext("CHAT")
    )

    class Factory(
        private val chatComponentFactory: ChatComponent.Factory
    ) : RootComponent.Factory {
        override fun invoke(componentContext: ComponentContext): RootComponent =
            DefaultRootComponent(
                componentContext = componentContext,
                chatComponentFactory = chatComponentFactory
            )
    }
}
