package org.speaksimpleapp.feature.chat.domain.model

data class ChatFeedback(
    val improvedText: String,
    val explanation: String,
    val suggestions: List<String>,
    val constructions: List<String>
)
