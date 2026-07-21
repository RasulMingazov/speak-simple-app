package org.speaksimpleapp.feature.chat.domain.entity

import kotlin.test.Test
import kotlin.test.assertEquals

class ChatMessageLimitTest {

    private val limit = ChatMessageLimit(
        recommendedLimit = 100,
        extraMessages = 5,
    )

    @Test
    fun resolvesAvailableBeforeRecommendedLimit() {
        assertEquals(
            MessageSendingAvailability.Available(messagesBeforeWarning = 1),
            limit.resolveAvailability(ChatUsage(acceptedUserMessageCount = 99)),
        )
    }

    @Test
    fun resolvesApproachingInsideExtraMessages() {
        assertEquals(
            MessageSendingAvailability.LimitApproaching(remainingMessages = 1),
            limit.resolveAvailability(ChatUsage(acceptedUserMessageCount = 104)),
        )
    }

    @Test
    fun resolvesReachedAtHardLimit() {
        assertEquals(
            MessageSendingAvailability.LimitReached,
            limit.resolveAvailability(ChatUsage(acceptedUserMessageCount = 105)),
        )
    }
}
