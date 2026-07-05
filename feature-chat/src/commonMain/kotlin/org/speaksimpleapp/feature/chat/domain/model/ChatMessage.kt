package org.speaksimpleapp.feature.chat.domain.model

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val text: String
)

enum class ChatRole {
    Assistant,
    User
}
