package org.speaksimpleapp.feature.chat.domain.model

data class ChatMessageLimit(
    val recommendedLimit: Int,
    val extraMessages: Int,
) {
    init {
        require(recommendedLimit > 0)
        require(extraMessages >= 0)
    }

    val hardLimit: Int
        get() = recommendedLimit + extraMessages
}

val DefaultChatMessageLimit = ChatMessageLimit(
    recommendedLimit = 100,
    extraMessages = 5,
)

data class ChatUsage(
    val acceptedUserMessageCount: Int,
) {
    init {
        require(acceptedUserMessageCount >= 0)
    }
}

sealed interface MessageSendingAvailability {
    data class Available(
        val messagesBeforeWarning: Int,
    ) : MessageSendingAvailability

    data class LimitApproaching(
        val remainingMessages: Int,
    ) : MessageSendingAvailability

    data object LimitReached : MessageSendingAvailability
}

fun ChatMessageLimit.resolveAvailability(
    usage: ChatUsage,
): MessageSendingAvailability {
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
