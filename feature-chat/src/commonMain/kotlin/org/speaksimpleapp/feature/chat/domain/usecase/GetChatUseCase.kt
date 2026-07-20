package org.speaksimpleapp.feature.chat.domain.usecase

import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

class GetChatUseCase(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(forceUpdate: Boolean) {
        chatRepository.getChat(forceUpdate = forceUpdate)
    }
}
