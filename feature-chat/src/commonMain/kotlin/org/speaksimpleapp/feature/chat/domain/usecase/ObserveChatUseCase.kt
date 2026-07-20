package org.speaksimpleapp.feature.chat.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.speaksimpleapp.feature.chat.domain.model.ChatSnapshot
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

class ObserveChatUseCase(
    private val chatRepository: ChatRepository,
) {
    operator fun invoke(): Flow<ChatSnapshot?> = chatRepository.observeChat()
}
