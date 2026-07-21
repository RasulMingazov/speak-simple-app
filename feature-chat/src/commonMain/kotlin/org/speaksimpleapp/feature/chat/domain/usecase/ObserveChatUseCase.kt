package org.speaksimpleapp.feature.chat.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.speaksimpleapp.feature.chat.domain.entity.ChatSnapshot
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

internal interface ObserveChatUseCase {
    operator fun invoke(): Flow<ChatSnapshot?>
}

internal class DefaultObserveChatUseCase(
    private val repository: ChatRepository,
) : ObserveChatUseCase {
    override fun invoke(): Flow<ChatSnapshot?> = repository.observeChat()
}
