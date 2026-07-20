package org.speaksimpleapp.feature.chat.presentation.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.speaksimpleapp.feature.chat.domain.model.ChatId
import org.speaksimpleapp.feature.chat.domain.model.MessageSendingAvailability

class ChatInputUiStateMapperTest {

    @Test
    fun mapsMessageAndSendingState() {
        val uiState = DefaultChatInputUiStateMapper(
            ChatInputModel.DataState(
                message = "Hello",
                isSending = true,
                chatId = ChatId("chat-1"),
                sendingAvailability = MessageSendingAvailability.Available(10),
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
                sendingAvailability = MessageSendingAvailability.Available(10),
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
                sendingAvailability = MessageSendingAvailability.LimitReached,
            )
        )

        assertFalse(uiState.canSend)
        assertTrue(uiState.isLimitReached)
    }
}
