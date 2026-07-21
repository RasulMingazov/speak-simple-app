package org.speaksimpleapp.feature.chat.domain.repository

import kotlinx.coroutines.flow.Flow
import org.speaksimpleapp.feature.chat.domain.entity.ChatSnapshot
import org.speaksimpleapp.feature.chat.domain.entity.SendMessageCommand
import org.speaksimpleapp.feature.chat.domain.entity.SendMessageResult

internal interface ChatRepository {
    fun observeChat(): Flow<ChatSnapshot?>

    suspend fun getChat(forceUpdate: Boolean)

    suspend fun sendMessage(command: SendMessageCommand): SendMessageResult
}
