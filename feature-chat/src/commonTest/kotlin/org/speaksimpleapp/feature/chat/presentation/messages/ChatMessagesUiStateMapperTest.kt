package org.speaksimpleapp.feature.chat.presentation.messages

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant
import org.speaksimpleapp.feature.chat.domain.entity.Chat
import org.speaksimpleapp.feature.chat.domain.entity.ChatId
import org.speaksimpleapp.feature.chat.domain.entity.ChatMessage
import org.speaksimpleapp.feature.chat.domain.entity.ClientMessageId
import org.speaksimpleapp.feature.chat.domain.entity.ChatSnapshot
import org.speaksimpleapp.feature.chat.domain.entity.ChatStatus
import org.speaksimpleapp.feature.chat.domain.entity.ChatUsage
import org.speaksimpleapp.feature.chat.domain.entity.DefaultChatMessageLimit
import org.speaksimpleapp.feature.chat.domain.entity.MessageAuthor
import org.speaksimpleapp.feature.chat.domain.entity.MessageId
import org.speaksimpleapp.feature.chat.domain.entity.MessageInputType

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
        assertEquals(ChatIdValue.value, state.chatId)
        assertEquals("Weekend plans", state.title)
        assertEquals(listOf(message.text), state.messageItems.map { it.text })
        assertEquals(
            listOf(ChatMessagesComponent.MessageType.Assistant),
            state.messageItems.map { it.type },
        )
        assertFalse(state.isMessageLimitReached)
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
