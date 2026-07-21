package org.speaksimpleapp.feature.chat.presentation.input

import org.speaksimpleapp.feature.chat.presentation.input.ChatInputModel.DataState

internal interface ChatInputUiStateMapper {
    operator fun invoke(dataState: DataState): ChatInputComponent.UiState
}

internal object DefaultChatInputUiStateMapper : ChatInputUiStateMapper {
    override fun invoke(dataState: DataState): ChatInputComponent.UiState =
        ChatInputComponent.UiState(
            message = dataState.message,
            isSending = dataState.isSending,
            canSend = dataState.message.isNotBlank() &&
                !dataState.isSending &&
                dataState.chatId != null &&
                !dataState.isMessageLimitReached,
            isLimitReached = dataState.isMessageLimitReached &&
                !dataState.isSending,
        )
}
