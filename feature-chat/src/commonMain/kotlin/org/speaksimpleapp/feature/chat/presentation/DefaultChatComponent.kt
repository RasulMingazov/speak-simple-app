package org.speaksimpleapp.feature.chat.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputComponent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent

internal class DefaultChatComponent(
    componentContext: ComponentContext,
    chatMessagesComponentFactory: ChatMessagesComponent.Factory,
    chatInputComponentFactory: ChatInputComponent.Factory,
) : ChatComponent, ComponentContext by componentContext {

    override val messages: ChatMessagesComponent = chatMessagesComponentFactory(
        componentContext = childContext("MESSAGES"),
    )
    override val input: ChatInputComponent = chatInputComponentFactory(
        componentContext = childContext("INPUT"),
    )

    class Factory(
        private val chatMessagesComponentFactory: ChatMessagesComponent.Factory,
        private val chatInputComponentFactory: ChatInputComponent.Factory,
    ) : ChatComponent.Factory {

        override fun invoke(componentContext: ComponentContext): ChatComponent =
            DefaultChatComponent(
                componentContext = componentContext,
                chatMessagesComponentFactory = chatMessagesComponentFactory,
                chatInputComponentFactory = chatInputComponentFactory,
            )
    }
}
