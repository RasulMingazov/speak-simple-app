package org.speaksimpleapp.feature.chat.domain.entity

import kotlin.time.Instant

internal data class ChatMessage(
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
