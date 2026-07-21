package org.speaksimpleapp.feature.chat.presentation.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.speaksimpleapp.feature.chat.domain.entity.ChatId
import org.speaksimpleapp.feature.chat.domain.entity.MessageSendingAvailability

class ChatInputUiStateMapperTest {

    @Test
    fun mapsMessageAndSendingState() {
        val uiState = DefaultChatInputUiStateMapper(
            ChatInputModel.DataState(
                message = "Hello",
                isSending = true,
                chatId = ChatId("chat-1"),
                isMessageLimitReached = false,
            )
        )

        assertEquals("Hello", uiState.message)
        assertTrue(uiState.isSending)
        assertFalse(uiState.canSend)
        assertFalse(uiState.isLimitReached)
    }

    @Test
    fun allowsSendingWhenMessageAndChatAreAvailable() {
        val uiState = DefaultChatInputUiStateMapper(
            ChatInputModel.DataState(
                message = "Hello",
                chatId = ChatId("chat-1"),
                isMessageLimitReached = false,
            )
        )

        assertTrue(uiState.canSend)
        assertFalse(uiState.isLimitReached)
    }

    @Test
    fun showsLimitReachedInsteadOfComposer() {
        val uiState = DefaultChatInputUiStateMapper(
            ChatInputModel.DataState(
                message = "Hello",
                chatId = ChatId("chat-1"),
                isMessageLimitReached = true,
            )
        )

        assertFalse(uiState.canSend)
        assertTrue(uiState.isLimitReached)
    }
}
