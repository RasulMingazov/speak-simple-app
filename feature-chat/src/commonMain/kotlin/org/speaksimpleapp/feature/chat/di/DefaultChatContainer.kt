package org.speaksimpleapp.feature.chat.di

import org.speaksimpleapp.core.common.coroutines.DefaultCoroutineDispatchers
import org.speaksimpleapp.feature.chat.data.repository.FakeChatRepository
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository
import org.speaksimpleapp.feature.chat.domain.usecase.DefaultGetChatUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.DefaultObserveChatUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.DefaultSendChatMessageUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.GetChatUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.ObserveChatUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.SendChatMessageUseCase
import org.speaksimpleapp.feature.chat.presentation.ChatComponent
import org.speaksimpleapp.feature.chat.presentation.DefaultChatComponent
import org.speaksimpleapp.feature.chat.presentation.input.DefaultChatInputComponent
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputModel
import org.speaksimpleapp.feature.chat.presentation.messages.DefaultChatMessagesComponent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesModel

internal class DefaultChatContainer {

    private val chatRepository: ChatRepository by lazy {
        FakeChatRepository()
    }

    private val getChatUseCase: GetChatUseCase by lazy {
        DefaultGetChatUseCase(chatRepository)
    }

    private val observeChatUseCase: ObserveChatUseCase by lazy {
        DefaultObserveChatUseCase(chatRepository)
    }

    private val sendChatMessageUseCase: SendChatMessageUseCase by lazy {
        DefaultSendChatMessageUseCase(chatRepository)
    }

    private val chatMessagesModelFactory = ChatMessagesModel.Factory(
        getChatUseCase = getChatUseCase,
        observeChatUseCase = observeChatUseCase,
        coroutineDispatchers = DefaultCoroutineDispatchers,
    )

    private val chatInputModelFactory = ChatInputModel.Factory(
        sendChatMessageUseCase = sendChatMessageUseCase,
        coroutineDispatchers = DefaultCoroutineDispatchers,
    )

    val chatComponentFactory: ChatComponent.Factory by lazy {
        DefaultChatComponent.Factory(
            chatMessagesComponentFactory = DefaultChatMessagesComponent.Factory(
                modelFactory = chatMessagesModelFactory,
            ),
            chatInputComponentFactory = DefaultChatInputComponent.Factory(
                modelFactory = chatInputModelFactory,
            ),
        )
    }
}
