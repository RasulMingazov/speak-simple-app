package org.speaksimpleapp.feature.chat.presentation

import org.speaksimpleapp.feature.chat.presentation.ChatViewModel.DataState

internal interface ChatUiStateMapper {
    operator fun invoke(dataState: DataState): ChatComponent.UiState
}

internal object DefaultChatUiStateMapper : ChatUiStateMapper {
    override fun invoke(dataState: DataState): ChatComponent.UiState =
        ChatComponent.UiState(
            inputMessage = dataState.inputMessage,
            messages = dataState.messages?.messages.orEmpty(),
            feedback = dataState.feedback,
            isInitialLoading = dataState.isInitialLoading,
            isPreviousLoading = dataState.isPreviousLoading,
            hasMorePrevious = dataState.messages?.hasMorePrevious ?: false,
            isSending = dataState.isSending
        )
}
