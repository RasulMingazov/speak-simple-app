package org.speaksimpleapp.feature.chat.domain.entity

internal sealed interface AssistantReplyResult {
    data class Success(val message: ChatMessage) : AssistantReplyResult
    data class Failed(val reason: AssistantReplyFailureReason) : AssistantReplyResult
}
