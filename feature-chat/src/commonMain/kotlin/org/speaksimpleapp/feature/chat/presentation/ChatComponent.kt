package org.speaksimpleapp.feature.chat.presentation

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage

interface ChatComponent {
    val uiState: StateFlow<UiState>
    val news: Flow<News>

    fun dispatch(uiEvent: Event)

    sealed interface Event {
        data class MessageChanged(val message: String) : Event
        data object SendClicked : Event
        data object LoadPreviousMessages : Event
    }

    sealed interface News {
        data object ScrollToBottom : News
    }

    data class UiState(
        val inputMessage: String,
        val messages: List<ChatMessage>,
        val feedback: ChatFeedback?,
        val isInitialLoading: Boolean,
        val isPreviousLoading: Boolean,
        val hasMorePrevious: Boolean,
        val isSending: Boolean
    ) {
        val canSend: Boolean = inputMessage.isNotBlank() && !isSending
        val canLoadPrevious: Boolean = hasMorePrevious && !isPreviousLoading && !isInitialLoading
    }

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): ChatComponent
    }
}
