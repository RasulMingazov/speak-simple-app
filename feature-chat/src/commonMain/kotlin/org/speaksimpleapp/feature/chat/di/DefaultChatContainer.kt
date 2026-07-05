package org.speaksimpleapp.feature.chat.di

import org.speaksimpleapp.feature.chat.data.FakeChatRepository
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository
import org.speaksimpleapp.feature.chat.domain.usecase.LoadChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.SendChatMessageUseCase
import org.speaksimpleapp.feature.chat.presentation.ChatComponent
import org.speaksimpleapp.feature.chat.presentation.DefaultChatComponent
import org.speaksimpleapp.feature.chat.presentation.DefaultChatDispatchers

class DefaultChatContainer : ChatContainer {

    private val chatRepository: ChatRepository by lazy {
        FakeChatRepository()
    }

    private val loadChatMessagesUseCase: LoadChatMessagesUseCase by lazy {
        LoadChatMessagesUseCase(chatRepository)
    }

    private val sendChatMessageUseCase: SendChatMessageUseCase by lazy {
        SendChatMessageUseCase(chatRepository)
    }

    override val chatComponentFactory: ChatComponent.Factory by lazy {
        DefaultChatComponent.Factory(
            loadChatMessagesUseCase = loadChatMessagesUseCase,
            sendChatMessageUseCase = sendChatMessageUseCase,
            chatDispatchers = DefaultChatDispatchers
        )
    }
}
