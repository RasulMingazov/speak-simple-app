package org.speaksimpleapp.feature.chat.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatMessages
import org.speaksimpleapp.feature.chat.domain.model.ChatRequest
import org.speaksimpleapp.feature.chat.domain.model.ChatRole
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

internal class FakeChatRepository : ChatRepository {

    private val history: List<ChatMessage> = buildHistory()
    private val localMessagesState = MutableStateFlow<ChatMessages?>(null)

    override fun observeMessages(): Flow<ChatMessages?> =
        localMessagesState.asStateFlow()

    override suspend fun loadMessages(
        beforeMessageId: String?,
        limit: Int,
        forceUpdate: Boolean
    ) {
        if (forceUpdate) {
            delay(650)
        }

        val endIndex = beforeMessageId
            ?.let { cursor -> history.indexOfFirst { it.id == cursor } }
            ?.takeIf { it >= 0 }
            ?: history.size
        val startIndex = (endIndex - limit).coerceAtLeast(0)
        val messages = history.subList(startIndex, endIndex)

        localMessagesState.update { currentMessages ->
            val updatedMessages = if (beforeMessageId == null) {
                messages
            } else {
                (messages + currentMessages.orEmpty().messages).distinctBy(ChatMessage::id)
            }

            ChatMessages(
                messages = updatedMessages,
                oldestMessageId = messages.firstOrNull()?.id ?: currentMessages?.oldestMessageId,
                hasMorePrevious = startIndex > 0
            )
        }
    }

    override suspend fun sendMessage(request: ChatRequest) {
        val text = request.text.trim()
        val userMessage = ChatMessage(
            id = "user-${localMessagesState.value.orEmpty().messages.size}",
            role = ChatRole.User,
            text = text,
            feedback = null
        )

        localMessagesState.update { messages ->
            ChatMessages(
                messages = messages.orEmpty().messages + userMessage,
                oldestMessageId = messages?.oldestMessageId,
                hasMorePrevious = messages?.hasMorePrevious ?: false
            )
        }

        delay(900)

        val improved = improve(text)
        val answer = "Got it. I understood: \"$text\". Try adding one detail or asking a follow-up question to keep the conversation natural."
        val feedback = ChatFeedback(
            improvedText = improved,
            explanation = "Your idea is understandable. A natural English message usually adds a little context and uses a softer phrase before the main point.",
            suggestions = listOf(
                "Add context: time, reason, place, or feeling.",
                "Use a soft opener like \"I was wondering...\".",
                "Make the message one complete sentence."
            ),
            constructions = listOf(
                "I was wondering if...",
                "What I mean is...",
                "It would be great to...",
                "Could you tell me more about..."
            )
        )

        localMessagesState.update { messages ->
            val currentMessages = messages.orEmpty()
            currentMessages.copy(
                messages = currentMessages.messages + listOf(
                    ChatMessage(
                        id = "assistant-${currentMessages.messages.size}",
                        role = ChatRole.Assistant,
                        text = answer,
                        feedback = null
                    ),
                    ChatMessage(
                        id = "feedback-${currentMessages.messages.size + 1}",
                        role = ChatRole.Feedback,
                        text = "",
                        feedback = feedback
                    )
                )
            )
        }
    }

    private fun improve(text: String): String {
        if (text.isBlank()) return text

        val normalized = text.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
        val punctuated = if (normalized.last() in ".!?") normalized else "$normalized."

        return "I was wondering, $punctuated Could you tell me more about it?"
    }

    private fun buildHistory(): List<ChatMessage> {
        val messages = mutableListOf<ChatMessage>()

        repeat(18) { index ->
            val number = index + 1
            messages += ChatMessage(
                id = "history-user-$number",
                role = ChatRole.User,
                text = sampleUserMessage(number),
                feedback = null
            )
            messages += ChatMessage(
                id = "history-assistant-$number",
                role = ChatRole.Assistant,
                text = sampleAssistantMessage(number),
                feedback = null
            )
        }

        messages += ChatMessage(
            id = "welcome-1",
            role = ChatRole.Assistant,
            text = "Hi! Send me a message in English, and I will help you make it sound more natural.",
            feedback = null
        )

        return messages
    }

    private fun sampleUserMessage(number: Int): String =
        when (number % 4) {
            0 -> "I want explain my idea about work."
            1 -> "Can you help me write this more natural?"
            2 -> "Yesterday I speak with my friend about travel."
            else -> "I need ask manager about deadline."
        }

    private fun sampleAssistantMessage(number: Int): String =
        when (number % 4) {
            0 -> "Sure. You could say: I would like to explain my idea about work in a clearer way."
            1 -> "Of course. Try adding the situation and the tone you want: casual, polite, or confident."
            2 -> "Nice. A more natural version is: Yesterday I talked with my friend about traveling."
            else -> "You can make it softer: Could you let me know what the deadline is?"
        }

    private fun ChatMessages?.orEmpty(): ChatMessages =
        this ?: ChatMessages(
            messages = emptyList(),
            oldestMessageId = null,
            hasMorePrevious = false
        )
}
