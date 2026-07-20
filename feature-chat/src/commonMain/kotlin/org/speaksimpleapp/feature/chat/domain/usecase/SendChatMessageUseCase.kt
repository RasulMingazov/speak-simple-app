package org.speaksimpleapp.feature.chat.domain.usecase

import org.speaksimpleapp.feature.chat.domain.model.ChatId
import org.speaksimpleapp.feature.chat.domain.model.ClientMessageId
import org.speaksimpleapp.feature.chat.domain.model.MessageInputType
import org.speaksimpleapp.feature.chat.domain.model.SendMessageCommand
import org.speaksimpleapp.feature.chat.domain.model.SendMessageResult
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

class SendChatMessageUseCase(
    private val chatRepository: ChatRepository
) {
    private var nextClientMessageNumber = 1L

    suspend operator fun invoke(
        chatId: ChatId,
        text: String,
        inputType: MessageInputType,
    ): SendMessageResult = chatRepository.sendMessage(
        command = SendMessageCommand(
            chatId = chatId,
            clientMessageId = ClientMessageId("client-${nextClientMessageNumber++}"),
            text = text,
            inputType = inputType,
        )
    )
}
