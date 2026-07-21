package org.speaksimpleapp.feature.chat.domain.entity

import kotlin.time.Instant

internal data class SpeechAudio(
    val messageId: MessageId,
    val audioUrl: String,
    val durationMillis: Long?,
    val expiresAt: Instant?,
)
