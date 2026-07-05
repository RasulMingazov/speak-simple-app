package org.speaksimpleapp.feature.chat.di

import org.speaksimpleapp.feature.chat.presentation.ChatComponent

interface ChatContainer {
    val chatComponentFactory: ChatComponent.Factory
}
