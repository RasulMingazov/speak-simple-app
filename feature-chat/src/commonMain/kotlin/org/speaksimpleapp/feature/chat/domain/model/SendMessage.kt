package org.speaksimpleapp.feature.chat.domain.model

data class SendMessageCommand(
    val chatId: ChatId,
    val clientMessageId: ClientMessageId,
    val text: String,
    val inputType: MessageInputType,
)

data class SendMessageResult(
    val userMessage: ChatMessage,
    val assistantReply: AssistantReplyResult,
    val usage: ChatUsage,
    val messageLimit: ChatMessageLimit,
)

sealed interface AssistantReplyResult {
    data class Success(
        val message: ChatMessage,
    ) : AssistantReplyResult

    data class Failed(
        val reason: AssistantReplyFailureReason,
    ) : AssistantReplyResult
}

enum class AssistantReplyFailureReason {
    MODEL_UNAVAILABLE,
    TIMEOUT,
    CONTENT_REJECTED,
    UNKNOWN,
}
