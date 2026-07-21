package org.speaksimpleapp.feature.chat.domain.usecase

import org.speaksimpleapp.feature.chat.domain.entity.ChatId
import org.speaksimpleapp.feature.chat.domain.entity.ClientMessageId
import org.speaksimpleapp.feature.chat.domain.entity.MessageInputType
import org.speaksimpleapp.feature.chat.domain.entity.SendMessageCommand
import org.speaksimpleapp.feature.chat.domain.entity.SendMessageResult
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

internal interface SendChatMessageUseCase {
    suspend operator fun invoke(
        chatId: ChatId,
        text: String,
        inputType: MessageInputType,
    ): SendMessageResult
}

internal class DefaultSendChatMessageUseCase(
    private val repository: ChatRepository,
) : SendChatMessageUseCase {
    private var nextClientMessageNumber = 1L

    override suspend fun invoke(
        chatId: ChatId,
        text: String,
        inputType: MessageInputType,
    ): SendMessageResult = repository.sendMessage(
        command = SendMessageCommand(
            chatId = chatId,
            clientMessageId = ClientMessageId("client-${nextClientMessageNumber++}"),
            text = text,
            inputType = inputType,
        ),
    )
}
