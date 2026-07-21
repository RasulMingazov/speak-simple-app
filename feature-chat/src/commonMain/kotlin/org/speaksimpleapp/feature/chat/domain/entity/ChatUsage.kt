package org.speaksimpleapp.feature.chat.domain.entity

internal data class ChatUsage(
    val acceptedUserMessageCount: Int,
) {
    init {
        require(acceptedUserMessageCount >= 0)
    }
}
