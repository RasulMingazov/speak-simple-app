package org.speaksimpleapp.feature.chat.presentation

import com.arkivanov.decompose.ComponentContext
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputComponent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent

interface ChatComponent {
    val messages: ChatMessagesComponent
    val input: ChatInputComponent

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): ChatComponent
    }
}
