package org.speaksimpleapp.feature.chat.domain.repository

import kotlinx.coroutines.flow.Flow
import org.speaksimpleapp.feature.chat.domain.model.ChatMessages
import org.speaksimpleapp.feature.chat.domain.model.ChatRequest
import org.speaksimpleapp.feature.chat.domain.model.ChatResponse

interface ChatRepository {
    fun observeMessages(): Flow<ChatMessages?>

    suspend fun loadMessages(
        beforeMessageId: String?,
        limit: Int,
        forceUpdate: Boolean
    )

    suspend fun sendMessage(request: ChatRequest): ChatResponse
}
