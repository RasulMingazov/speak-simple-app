package org.speaksimpleapp.feature.chat.domain.entity

internal data class ChatSnapshot(
    val chat: Chat,
    val messages: List<ChatMessage>,
    val messageLimit: ChatMessageLimit,
    val usage: ChatUsage,
) {
    val sendingAvailability: MessageSendingAvailability
        get() = messageLimit.resolveAvailability(usage)
}
