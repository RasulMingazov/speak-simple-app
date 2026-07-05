package org.speaksimpleapp.feature.chat.domain.usecase

import org.speaksimpleapp.feature.chat.domain.model.ChatMessagesPage
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

class LoadChatMessagesUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        beforeMessageId: String?,
        limit: Int
    ): ChatMessagesPage =
        chatRepository.loadMessages(
            beforeMessageId = beforeMessageId,
            limit = limit
        )
}
