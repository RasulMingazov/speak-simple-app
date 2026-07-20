package org.speaksimpleapp.feature.chat.domain.model

data class MessageSuggestion(
    val id: String,
    val messageId: MessageId,
    val originalText: String,
    val correctedText: String,
    val explanation: String,
    val category: SuggestionCategory,
)

enum class SuggestionCategory {
    GRAMMAR,
    VOCABULARY,
    WORD_ORDER,
    STYLE,
    CLARITY,
}
