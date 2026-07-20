package org.speaksimpleapp.feature.chat.domain.model

import kotlin.time.Instant

data class SpeechAudio(
    val messageId: MessageId,
    val audioUrl: String,
    val durationMillis: Long?,
    val expiresAt: Instant?,
)
