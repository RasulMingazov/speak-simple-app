package org.speaksimpleapp.feature.chat.domain.model

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val text: String,
    val feedback: ChatFeedback?
)

enum class ChatRole {
    Assistant,
    User,
    Feedback,
}
