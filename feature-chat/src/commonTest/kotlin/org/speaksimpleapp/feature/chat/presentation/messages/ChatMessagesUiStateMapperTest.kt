package org.speaksimpleapp.feature.chat.presentation.messages

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant
import org.speaksimpleapp.feature.chat.domain.model.Chat
import org.speaksimpleapp.feature.chat.domain.model.ChatId
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ClientMessageId
import org.speaksimpleapp.feature.chat.domain.model.ChatSnapshot
import org.speaksimpleapp.feature.chat.domain.model.ChatStatus
import org.speaksimpleapp.feature.chat.domain.model.ChatUsage
import org.speaksimpleapp.feature.chat.domain.model.DefaultChatMessageLimit
import org.speaksimpleapp.feature.chat.domain.model.MessageAuthor
import org.speaksimpleapp.feature.chat.domain.model.MessageId
import org.speaksimpleapp.feature.chat.domain.model.MessageInputType

class ChatMessagesUiStateMapperTest {

    @Test
    fun mapsInitialLoading() {
        val state = DefaultChatMessagesUiStateMapper(
            ChatMessagesModel.DataState(),
        )

        assertTrue(state.isInitialLoading)
        assertTrue(state.messageItems.isEmpty())
        assertEquals(null, state.assistantTypingKey)
    }

    @Test
    fun mapsChatTitleAndMessages() {
        val message = message()
        val state = DefaultChatMessagesUiStateMapper(
            ChatMessagesModel.DataState(
                snapshot = snapshot(message),
            ),
        )

        assertFalse(state.isInitialLoading)
        assertEquals(ChatIdValue, state.chatId)
        assertEquals("Weekend plans", state.title)
        assertEquals(listOf(message.text), state.messageItems.map { it.text })
        assertEquals(
            listOf(ChatMessagesComponent.MessageType.Assistant),
            state.messageItems.map { it.type },
        )
        assertEquals(snapshot(message).sendingAvailability, state.sendingAvailability)
    }

    @Test
    fun mapsAssistantReplyToPreviousUserStableKey() {
        val userMessage = message(
            id = "user-1",
            author = MessageAuthor.USER,
        )
        val assistantMessage = message(
            id = "assistant-1",
            author = MessageAuthor.ASSISTANT,
        )

        val state = DefaultChatMessagesUiStateMapper(
            ChatMessagesModel.DataState(
                snapshot = snapshot(userMessage, assistantMessage),
            ),
        )

        assertEquals("user-1", state.messageItems[0].key)
        assertEquals("assistant-for-user-1", state.messageItems[1].key)
        assertTrue(state.messageItems[1].animateAppearance)
    }

    @Test
    fun mapsAssistantTypingKeyWhenLastMessageIsUser() {
        val state = DefaultChatMessagesUiStateMapper(
            ChatMessagesModel.DataState(
                snapshot = snapshot(
                    message(
                        id = "user-1",
                        author = MessageAuthor.USER,
                    ),
                ),
            ),
        )

        assertEquals("assistant-for-user-1", state.assistantTypingKey)
        assertTrue(state.messageItems.single().animateAppearance)
    }

    private fun snapshot(vararg messages: ChatMessage): ChatSnapshot = ChatSnapshot(
        chat = Chat(
            id = ChatIdValue,
            title = "Weekend plans",
            status = ChatStatus.ACTIVE,
            createdAt = CreatedAt,
            updatedAt = CreatedAt,
        ),
        messages = messages.toList(),
        messageLimit = DefaultChatMessageLimit,
        usage = ChatUsage(acceptedUserMessageCount = 0),
    )

    private fun message(
        id: String = "message-1",
        author: MessageAuthor = MessageAuthor.ASSISTANT,
    ): ChatMessage = ChatMessage(
        id = MessageId(id),
        chatId = ChatIdValue,
        clientMessageId = if (author == MessageAuthor.USER) ClientMessageId("client-$id") else null,
        author = author,
        text = "Hi",
        inputType = MessageInputType.TEXT,
        createdAt = CreatedAt,
        suggestionCount = 0,
    )

    private companion object {
        val ChatIdValue = ChatId("chat-1")
        val CreatedAt = Instant.parse("2026-07-19T09:00:00Z")
    }
}
