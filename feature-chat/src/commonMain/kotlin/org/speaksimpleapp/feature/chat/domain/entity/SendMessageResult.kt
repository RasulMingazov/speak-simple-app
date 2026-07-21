package org.speaksimpleapp.feature.chat.domain.entity

internal data class SendMessageResult(
    val userMessage: ChatMessage,
    val assistantReply: AssistantReplyResult,
    val usage: ChatUsage,
    val messageLimit: ChatMessageLimit,
)
