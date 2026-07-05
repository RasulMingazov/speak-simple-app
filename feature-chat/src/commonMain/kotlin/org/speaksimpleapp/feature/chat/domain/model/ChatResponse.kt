package org.speaksimpleapp.feature.chat.domain.model

data class ChatResponse(
    val answer: String,
    val feedback: ChatFeedback
)
