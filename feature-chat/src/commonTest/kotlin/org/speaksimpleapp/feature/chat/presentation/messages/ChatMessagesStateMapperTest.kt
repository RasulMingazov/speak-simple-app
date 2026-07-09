package org.speaksimpleapp.feature.chat.presentation.messages

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatMessages
import org.speaksimpleapp.feature.chat.domain.model.ChatRole

class ChatMessagesStateMapperTest {

    @Test
    fun showsInitialLoadingFromDataState() {
        val state = DefaultChatMessagesStateMapper(
            ChatMessagesModel.DataState(
                messages = ChatMessages(
                    messages = emptyList(),
                    oldestMessageId = null,
                    hasMorePrevious = false
                ),
                isInitialLoading = true
            )
        )

        assertTrue(state.isInitialLoading)
    }

    @Test
    fun mapsMessagesAndPagination() {
        val state = DefaultChatMessagesStateMapper(
            ChatMessagesModel.DataState(
                messages = ChatMessages(
                    messages = listOf(
                        ChatMessage(
                            id = "1",
                            role = ChatRole.Assistant,
                            text = "Hi",
                            feedback = null
                        )
                    ),
                    oldestMessageId = "1",
                    hasMorePrevious = true
                ),
                isInitialLoading = false
            )
        )

        assertFalse(state.isInitialLoading)
        assertTrue(state.hasMorePrevious)
        assertTrue(state.canLoadPrevious)
        assertTrue(state.previousPageKey == "1")
    }

    private fun message(id: String): ChatMessage =
        ChatMessage(
            id = id,
            role = ChatRole.Assistant,
            text = "Hi",
            feedback = null
        )
}
