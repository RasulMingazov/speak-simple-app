package org.speaksimpleapp.feature.chat.domain.model

import kotlin.time.Instant

data class Chat(
    val id: ChatId,
    val title: String,
    val status: ChatStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)

enum class ChatStatus {
    ACTIVE,
    COMPLETED,
    ARCHIVED,
}
