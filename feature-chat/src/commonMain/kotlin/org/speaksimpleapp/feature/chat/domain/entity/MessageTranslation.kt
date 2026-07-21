package org.speaksimpleapp.feature.chat.domain.entity

internal data class MessageTranslation(
    val messageId: MessageId,
    val sourceLanguage: String,
    val targetLanguage: String,
    val translatedText: String,
)
