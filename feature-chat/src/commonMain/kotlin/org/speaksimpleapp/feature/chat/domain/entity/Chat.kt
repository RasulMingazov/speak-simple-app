package org.speaksimpleapp.feature.chat.domain.entity

import kotlin.time.Instant

internal data class Chat(
    val id: ChatId,
    val title: String,
    val status: ChatStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)
