package org.speaksimpleapp.feature.chat.presentation.messages

import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesModel.DataState

internal interface ChatMessagesStateMapper {
    operator fun invoke(dataState: DataState): ChatMessagesComponent.UiState
}

internal object DefaultChatMessagesStateMapper : ChatMessagesStateMapper {
    override fun invoke(dataState: DataState): ChatMessagesComponent.UiState =
        ChatMessagesComponent.UiState(
            messages = dataState.messages?.messages.orEmpty(),
            isInitialLoading = dataState.isInitialLoading,
            isPreviousLoading = dataState.isPreviousLoading,
            hasMorePrevious = dataState.messages?.hasMorePrevious ?: false,
            previousPageKey = previousPageKey(dataState)
        )

    private fun previousPageKey(dataState: DataState): String? {
        val messages = dataState.messages ?: return null
        if (dataState.isInitialLoading) return null
        if (dataState.isPreviousLoading) return null
        if (!messages.hasMorePrevious) return null
        return messages.oldestMessageId
    }
}
