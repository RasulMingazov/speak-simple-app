package org.speaksimpleapp.feature.chat.domain.model

data class ChatMessagesPage(
    val messages: List<ChatMessage>,
    val nextCursor: String?,
    val hasMore: Boolean
)
