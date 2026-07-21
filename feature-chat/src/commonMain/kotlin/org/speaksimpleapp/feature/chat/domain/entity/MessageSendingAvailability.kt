package org.speaksimpleapp.feature.chat.domain.entity

internal sealed interface MessageSendingAvailability {
    data class Available(val messagesBeforeWarning: Int) : MessageSendingAvailability
    data class LimitApproaching(val remainingMessages: Int) : MessageSendingAvailability
    data object LimitReached : MessageSendingAvailability
}
