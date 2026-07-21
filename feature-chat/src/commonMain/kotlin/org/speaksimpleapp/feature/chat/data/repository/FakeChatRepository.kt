package org.speaksimpleapp.feature.chat.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Instant
import org.speaksimpleapp.feature.chat.domain.entity.AssistantReplyResult
import org.speaksimpleapp.feature.chat.domain.entity.Chat
import org.speaksimpleapp.feature.chat.domain.entity.ChatId
import org.speaksimpleapp.feature.chat.domain.entity.ChatMessage
import org.speaksimpleapp.feature.chat.domain.entity.ChatSnapshot
import org.speaksimpleapp.feature.chat.domain.entity.ChatStatus
import org.speaksimpleapp.feature.chat.domain.entity.ChatUsage
import org.speaksimpleapp.feature.chat.domain.entity.ClientMessageId
import org.speaksimpleapp.feature.chat.domain.entity.DefaultChatMessageLimit
import org.speaksimpleapp.feature.chat.domain.entity.MessageAuthor
import org.speaksimpleapp.feature.chat.domain.entity.MessageId
import org.speaksimpleapp.feature.chat.domain.entity.MessageInputType
import org.speaksimpleapp.feature.chat.domain.entity.MessageSendingAvailability
import org.speaksimpleapp.feature.chat.domain.entity.SendMessageCommand
import org.speaksimpleapp.feature.chat.domain.entity.SendMessageResult
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

internal class FakeChatRepository : ChatRepository {

    private val chatState = MutableStateFlow<ChatSnapshot?>(null)
    private var nextMessageNumber = InitialMessages.size + 1

    override fun observeChat(): Flow<ChatSnapshot?> = chatState.asStateFlow()

    override suspend fun getChat(forceUpdate: Boolean) {
        if (forceUpdate) delay(ChatLoadingDelayMillis)
        if (chatState.value == null) chatState.value = InitialSnapshot
    }

    override suspend fun sendMessage(command: SendMessageCommand): SendMessageResult {
        val snapshot = requireNotNull(chatState.value)
        check(snapshot.chat.id == command.chatId)
        check(snapshot.sendingAvailability != MessageSendingAvailability.LimitReached)

        val userMessage = ChatMessage(
            id = nextMessageId(MessageAuthor.USER),
            chatId = command.chatId,
            clientMessageId = command.clientMessageId,
            author = MessageAuthor.USER,
            text = command.text.trim(),
            inputType = command.inputType,
            createdAt = nextCreatedAt(),
            suggestionCount = 0,
        )
        val updatedUsage = ChatUsage(
            acceptedUserMessageCount = snapshot.usage.acceptedUserMessageCount + 1,
        )

        chatState.value = snapshot.copy(
            messages = snapshot.messages + userMessage,
            usage = updatedUsage,
        )

        delay(AssistantReplyDelayMillis)

        val analyzedUserMessage = userMessage.copy(
            suggestionCount = suggestionCount(command.text),
        )
        val assistantMessage = ChatMessage(
            id = nextMessageId(MessageAuthor.ASSISTANT),
            chatId = command.chatId,
            clientMessageId = null,
            author = MessageAuthor.ASSISTANT,
            text = createContextualAnswer(command.text),
            inputType = MessageInputType.TEXT,
            createdAt = nextCreatedAt(),
            suggestionCount = 0,
        )

        val currentSnapshot = requireNotNull(chatState.value)
        chatState.value = currentSnapshot.copy(
            messages = currentSnapshot.messages.map { message ->
                if (message.id == userMessage.id) analyzedUserMessage else message
            } + assistantMessage,
            usage = updatedUsage,
        )

        return SendMessageResult(
            userMessage = analyzedUserMessage,
            assistantReply = AssistantReplyResult.Success(assistantMessage),
            usage = updatedUsage,
            messageLimit = snapshot.messageLimit,
        )
    }

    private fun nextMessageId(author: MessageAuthor): MessageId =
        MessageId("${author.name.lowercase()}-${nextMessageNumber++}")

    private fun nextCreatedAt(): Instant =
        Instant.fromEpochMilliseconds(BaseTimestampMillis + nextMessageNumber * 1_000L)
}

private val ChatIdValue = ChatId("weekend-plans")
private val BaseCreatedAt = Instant.parse("2026-07-19T09:00:00Z")

private val InitialMessages = listOf(
    chatMessage(
        id = "assistant-1",
        author = MessageAuthor.ASSISTANT,
        text = "You just finished a team call. Try asking a colleague how their week is going.",
    ),
    chatMessage(
        id = "user-2",
        author = MessageAuthor.USER,
        text = "How is your week going so far?",
        suggestionCount = 2,
    ),
    chatMessage(
        id = "assistant-3",
        author = MessageAuthor.ASSISTANT,
        text = "Pretty good, thanks. It’s been a busy week, but I finally wrapped up a project. How about you?",
    ),
    chatMessage(
        id = "user-4",
        author = MessageAuthor.USER,
        text = "It has been busy, but I’m learning a lot from the new project.",
        suggestionCount = 1,
    ),
    chatMessage(
        id = "assistant-5",
        author = MessageAuthor.ASSISTANT,
        text = "That sounds productive. What part of the new project has been the most interesting so far?",
    ),
)

private val InitialSnapshot = ChatSnapshot(
    chat = Chat(
        id = ChatIdValue,
        title = "Weekend plans",
        status = ChatStatus.ACTIVE,
        createdAt = BaseCreatedAt,
        updatedAt = BaseCreatedAt,
    ),
    messages = InitialMessages,
    messageLimit = DefaultChatMessageLimit,
    usage = ChatUsage(acceptedUserMessageCount = 2),
)

private fun chatMessage(
    id: String,
    author: MessageAuthor,
    text: String,
    suggestionCount: Int = 0,
): ChatMessage = ChatMessage(
    id = MessageId(id),
    chatId = ChatIdValue,
    clientMessageId = if (author == MessageAuthor.USER) {
        ClientMessageId("client-$id")
    } else {
        null
    },
    author = author,
    text = text,
    inputType = MessageInputType.TEXT,
    createdAt = BaseCreatedAt,
    suggestionCount = suggestionCount,
)

private fun suggestionCount(text: String): Int =
    if (text.length > LongMessageLength) 2 else 1

private fun createContextualAnswer(text: String): String = when {
    text.contains("week", ignoreCase = true) ->
        "Pretty good, thanks. It’s been a busy week, but I finally wrapped up a project. How about you?"

    text.contains("project", ignoreCase = true) ->
        "That sounds productive. What part of the new project has been the most interesting so far?"

    else -> "That makes sense. Could you tell me a little more about it?"
}

private const val ChatLoadingDelayMillis = 650L
private const val AssistantReplyDelayMillis = 900L
private const val LongMessageLength = 48
private const val BaseTimestampMillis = 1_752_916_400_000L
