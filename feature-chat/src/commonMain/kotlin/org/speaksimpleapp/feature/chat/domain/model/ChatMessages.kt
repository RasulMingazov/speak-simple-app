package org.speaksimpleapp.feature.chat.domain.model

data class ChatMessages(
    val messages: List<ChatMessage>,
    val oldestMessageId: String?,
    val hasMorePrevious: Boolean
)
