package org.speaksimpleapp.feature.chat.presentation.messages

import org.speaksimpleapp.feature.chat.domain.entity.ChatMessage
import org.speaksimpleapp.feature.chat.domain.entity.MessageAuthor
import org.speaksimpleapp.feature.chat.domain.entity.MessageSendingAvailability
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesModel.DataState

internal interface ChatMessagesUiStateMapper {
    operator fun invoke(dataState: DataState): ChatMessagesComponent.UiState
}

internal object DefaultChatMessagesUiStateMapper : ChatMessagesUiStateMapper {
    override fun invoke(dataState: DataState): ChatMessagesComponent.UiState =
        ChatMessagesComponent.UiState(
            chatId = dataState.snapshot?.chat?.id?.value,
            title = dataState.snapshot?.chat?.title.orEmpty(),
            messageItems = dataState.snapshot?.messages.orEmpty().toMessageItems(),
            assistantTypingKey = dataState.snapshot?.messages.orEmpty().assistantTypingKey(),
            isMessageLimitReached =
                dataState.snapshot?.sendingAvailability == MessageSendingAvailability.LimitReached,
            isInitialLoading = dataState.snapshot == null,
        )

    private fun List<ChatMessage>.toMessageItems(): List<ChatMessagesComponent.MessageItem> =
        mapIndexed { index, message ->
            val previousMessage = getOrNull(index - 1)
            ChatMessagesComponent.MessageItem(
                key = message.itemKey(previousMessage),
                text = message.text,
                type = message.toMessageType(),
                suggestionCount = message.suggestionCount,
                animateAppearance = message.shouldAnimateAppearance(
                    previousMessage = previousMessage,
                    isLastMessage = index == lastIndex,
                ),
            )
        }

    private fun ChatMessage.toMessageType(): ChatMessagesComponent.MessageType = when (author) {
        MessageAuthor.USER -> ChatMessagesComponent.MessageType.User
        MessageAuthor.ASSISTANT -> ChatMessagesComponent.MessageType.Assistant
        MessageAuthor.SYSTEM -> ChatMessagesComponent.MessageType.System
    }

    private fun ChatMessage.itemKey(previousMessage: ChatMessage?): String =
        if (author == MessageAuthor.ASSISTANT && previousMessage?.author == MessageAuthor.USER) {
            previousMessage.assistantReplyKey()
        } else {
            id.value
        }

    private fun ChatMessage.shouldAnimateAppearance(
        previousMessage: ChatMessage?,
        isLastMessage: Boolean,
    ): Boolean = isLastMessage && when (author) {
        MessageAuthor.USER -> true
        MessageAuthor.ASSISTANT -> previousMessage?.author == MessageAuthor.USER
        MessageAuthor.SYSTEM -> false
    }

    private fun List<ChatMessage>.assistantTypingKey(): String? =
        lastOrNull()
            ?.takeIf { it.author == MessageAuthor.USER }
            ?.assistantReplyKey()

    private fun ChatMessage.assistantReplyKey(): String = "assistant-for-${id.value}"
}
