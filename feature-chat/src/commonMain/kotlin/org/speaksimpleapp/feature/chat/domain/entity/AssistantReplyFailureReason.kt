package org.speaksimpleapp.feature.chat.domain.entity

internal enum class AssistantReplyFailureReason {
    MODEL_UNAVAILABLE,
    TIMEOUT,
    CONTENT_REJECTED,
    UNKNOWN,
}
