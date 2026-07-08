package org.speaksimpleapp.feature.chat.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.speaksimpleapp.feature.chat.domain.model.ChatMessages
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

class ObserveChatMessagesUseCase(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<ChatMessages?> =
        chatRepository.observeMessages()
}
