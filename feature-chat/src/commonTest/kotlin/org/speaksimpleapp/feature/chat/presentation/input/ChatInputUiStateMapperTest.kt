package org.speaksimpleapp.feature.chat.presentation.input

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChatInputUiStateMapperTest {

    @Test
    fun allowsSendWhenMessageIsNotBlankAndNotSending() {
        val uiState = DefaultChatInputUiStateMapper(
            ChatInputModel.DataState(
                message = "Hello",
                isSending = false
            )
        )

        assertTrue(uiState.canSend)
    }

    @Test
    fun disablesSendWhenMessageIsBlank() {
        val uiState = DefaultChatInputUiStateMapper(
            ChatInputModel.DataState(
                message = "   ",
                isSending = false
            )
        )

        assertFalse(uiState.canSend)
    }

    @Test
    fun disablesSendWhileSending() {
        val uiState = DefaultChatInputUiStateMapper(
            ChatInputModel.DataState(
                message = "Hello",
                isSending = true
            )
        )

        assertFalse(uiState.canSend)
    }
}
