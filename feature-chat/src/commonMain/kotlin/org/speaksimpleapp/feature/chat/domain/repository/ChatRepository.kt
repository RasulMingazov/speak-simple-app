package org.speaksimpleapp.feature.chat.domain.repository

import kotlinx.coroutines.flow.Flow
import org.speaksimpleapp.feature.chat.domain.model.ChatSnapshot
import org.speaksimpleapp.feature.chat.domain.model.SendMessageCommand
import org.speaksimpleapp.feature.chat.domain.model.SendMessageResult

interface ChatRepository {
    fun observeChat(): Flow<ChatSnapshot?>

    suspend fun getChat(forceUpdate: Boolean)

    suspend fun sendMessage(command: SendMessageCommand): SendMessageResult
}
