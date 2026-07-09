package org.speaksimpleapp.feature.chat.presentation.messages

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage

interface ChatMessagesComponent {
    val uiState: StateFlow<UiState>
    val news: Flow<News>

    fun dispatch(event: Event)

    sealed interface Event {
        class LoadPreviousMessages(val beforeMessageId: String) : Event
    }

    sealed interface News {
        data object ScrollToBottom : News
    }

    data class UiState(
        val messages: List<ChatMessage>,
        val isInitialLoading: Boolean,
        val isPreviousLoading: Boolean,
        val hasMorePrevious: Boolean,
        val previousPageKey: String?
    ) {
        val canLoadPrevious: Boolean = previousPageKey != null
    }

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): ChatMessagesComponent
    }
}
