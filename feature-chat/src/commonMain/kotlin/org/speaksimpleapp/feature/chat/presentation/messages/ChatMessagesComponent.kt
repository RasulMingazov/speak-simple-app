package org.speaksimpleapp.feature.chat.presentation.messages

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ChatMessagesComponent {
    val uiState: StateFlow<UiState>
    val news: Flow<News>

    sealed interface News {
        data object ScrollToBottom : News
    }

    data class UiState(
        val chatId: String?,
        val title: String,
        val messageItems: List<MessageItem>,
        val assistantTypingKey: String?,
        val isMessageLimitReached: Boolean,
        val isInitialLoading: Boolean,
    )

    data class MessageItem(
        val key: String,
        val text: String,
        val type: MessageType,
        val suggestionCount: Int,
        val animateAppearance: Boolean,
    )

    enum class MessageType {
        User,
        Assistant,
        System,
    }

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): ChatMessagesComponent
    }
}
