package org.speaksimpleapp.feature.chat.data

import kotlinx.coroutines.delay
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatMessagesPage
import org.speaksimpleapp.feature.chat.domain.model.ChatRequest
import org.speaksimpleapp.feature.chat.domain.model.ChatResponse
import org.speaksimpleapp.feature.chat.domain.model.ChatRole
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository

internal class FakeChatRepository : ChatRepository {

    private val history: List<ChatMessage> = buildHistory()

    override suspend fun loadMessages(
        beforeMessageId: String?,
        limit: Int
    ): ChatMessagesPage {
        delay(650)

        val endIndex = beforeMessageId
            ?.let { cursor -> history.indexOfFirst { it.id == cursor } }
            ?.takeIf { it >= 0 }
            ?: history.size
        val startIndex = (endIndex - limit).coerceAtLeast(0)
        val messages = history.subList(startIndex, endIndex)

        return ChatMessagesPage(
            messages = messages,
            nextCursor = messages.firstOrNull()?.id,
            hasMore = startIndex > 0
        )
    }

    override suspend fun sendMessage(request: ChatRequest): ChatResponse {
        delay(900)

        val text = request.text.trim()
        val improved = improve(text)

        return ChatResponse(
            answer = "Got it. I understood: \"$text\". Try adding one detail or asking a follow-up question to keep the conversation natural.",
            feedback = ChatFeedback(
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
        )
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
                text = sampleUserMessage(number)
            )
            messages += ChatMessage(
                id = "history-assistant-$number",
                role = ChatRole.Assistant,
                text = sampleAssistantMessage(number)
            )
        }

        messages += ChatMessage(
            id = "welcome-1",
            role = ChatRole.Assistant,
            text = "Hi! Send me a message in English, and I will help you make it sound more natural."
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
}
