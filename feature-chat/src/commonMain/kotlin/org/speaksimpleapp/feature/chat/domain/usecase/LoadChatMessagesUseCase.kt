package org.speaksimpleapp.feature.chat.domain.usecase

import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

class LoadChatMessagesUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        beforeMessageId: String? = null,
        limit: Int = DEFAULT_LIMIT,
        forceUpdate: Boolean
    ) {
        chatRepository.loadMessages(
            beforeMessageId = beforeMessageId,
            limit = limit,
            forceUpdate = forceUpdate
        )
    }

    private companion object {
        const val DEFAULT_LIMIT = 12
    }
}
