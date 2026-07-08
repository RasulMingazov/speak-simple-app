package org.speaksimpleapp.feature.chat.presentation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatMessages
import org.speaksimpleapp.feature.chat.domain.model.ChatRole

class ChatUiStateMapperTest {

    @Test
    fun showsInitialLoadingFromDataState() {
        val uiState = DefaultChatUiStateMapper(
            ChatViewModel.DataState(
                messages = ChatMessages(
                    messages = emptyList(),
                    oldestMessageId = null,
                    hasMorePrevious = false
                ),
                isInitialLoading = true
            )
        )

        assertTrue(uiState.isInitialLoading)
    }

    @Test
    fun mapsMessagesAndPagination() {
        val uiState = DefaultChatUiStateMapper(
            ChatViewModel.DataState(
                messages = ChatMessages(
                    messages = listOf(
                        ChatMessage(
                            id = "1",
                            role = ChatRole.Assistant,
                            text = "Hi"
                        )
                    ),
                    oldestMessageId = "1",
                    hasMorePrevious = true
                ),
                isInitialLoading = false
            )
        )

        assertFalse(uiState.isInitialLoading)
        assertTrue(uiState.hasMorePrevious)
    }
}
