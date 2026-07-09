package org.speaksimpleapp.feature.chat.domain.usecase

import org.speaksimpleapp.feature.chat.domain.model.ChatRequest
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

class SendChatMessageUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(text: String) {
        chatRepository.sendMessage(
            request = ChatRequest(
                text = text
            )
        )
    }
}
