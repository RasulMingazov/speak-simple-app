package org.speaksimpleapp.feature.chat.domain.entity

internal data class ChatMessageLimit(
    val recommendedLimit: Int,
    val extraMessages: Int,
) {
    init {
        require(recommendedLimit > 0)
        require(extraMessages >= 0)
    }

    val hardLimit: Int
        get() = recommendedLimit + extraMessages

    fun resolveAvailability(usage: ChatUsage): MessageSendingAvailability {
        val count = usage.acceptedUserMessageCount

        return when {
            count < recommendedLimit -> MessageSendingAvailability.Available(
                messagesBeforeWarning = recommendedLimit - count,
            )
            count < hardLimit -> MessageSendingAvailability.LimitApproaching(
                remainingMessages = hardLimit - count,
            )
            else -> MessageSendingAvailability.LimitReached
        }
    }
}
