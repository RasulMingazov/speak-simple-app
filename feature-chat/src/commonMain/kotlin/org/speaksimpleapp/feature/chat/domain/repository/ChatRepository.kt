package org.speaksimpleapp.feature.chat.domain.repository

import kotlinx.coroutines.flow.Flow
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatMessagesPage
import org.speaksimpleapp.feature.chat.domain.model.ChatRequest
import org.speaksimpleapp.feature.chat.domain.model.ChatResponse

interface ChatRepository {
    fun observeMessages(): Flow<List<ChatMessage>>

    suspend fun loadMessages(
        beforeMessageId: String?,
        limit: Int
    ): ChatMessagesPage

    suspend fun sendMessage(request: ChatRequest): ChatResponse
}
