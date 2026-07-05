package org.speaksimpleapp.feature.chat.domain.model

data class ChatRequest(
    val text: String,
    val context: List<ChatMessage>
)
