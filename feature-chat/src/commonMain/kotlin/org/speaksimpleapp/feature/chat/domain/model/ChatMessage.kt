package org.speaksimpleapp.feature.chat.domain.model

import kotlin.time.Instant

data class ChatMessage(
    val id: MessageId,
    val chatId: ChatId,
    val clientMessageId: ClientMessageId?,
    val author: MessageAuthor,
    val text: String,
    val inputType: MessageInputType,
    val createdAt: Instant,
    val suggestionCount: Int,
) {
    init {
        require(text.isNotBlank())
        require(suggestionCount >= 0)
        require((author == MessageAuthor.USER) == (clientMessageId != null))
    }
}

enum class MessageAuthor {
    USER,
    ASSISTANT,
    SYSTEM,
}

enum class MessageInputType {
    TEXT,
    VOICE_TRANSCRIPT,
}
