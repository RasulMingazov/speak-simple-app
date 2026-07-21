package org.speaksimpleapp.feature.chat.domain.usecase

import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

internal interface GetChatUseCase {
    suspend operator fun invoke(forceUpdate: Boolean)
}

internal class DefaultGetChatUseCase(
    private val repository: ChatRepository,
) : GetChatUseCase {
    override suspend fun invoke(forceUpdate: Boolean) {
        repository.getChat(forceUpdate = forceUpdate)
    }
}
