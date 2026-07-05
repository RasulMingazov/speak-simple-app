package org.speaksimpleapp.feature.chat.domain.usecase

import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatRequest
import org.speaksimpleapp.feature.chat.domain.model.ChatResponse
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

class SendChatMessageUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        text: String,
        context: List<ChatMessage>
    ): ChatResponse =
        chatRepository.sendMessage(
            request = ChatRequest(
                text = text,
                context = context
            )
        )
}
