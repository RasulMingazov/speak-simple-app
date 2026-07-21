package org.speaksimpleapp.feature.chat.domain.entity

internal data class SendMessageCommand(
    val chatId: ChatId,
    val clientMessageId: ClientMessageId,
    val text: String,
    val inputType: MessageInputType,
)
