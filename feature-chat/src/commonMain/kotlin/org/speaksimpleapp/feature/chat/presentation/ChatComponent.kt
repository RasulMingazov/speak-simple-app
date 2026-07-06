package org.speaksimpleapp.feature.chat.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.Flow
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage

interface ChatComponent {
    val uiState: Value<UiState>
    val news: Flow<UiNews>

    fun handle(uiEvent: UiEvent)

    sealed interface UiEvent {
        data class MessageChanged(val message: String) : UiEvent
        data object SendClicked : UiEvent
        data object LoadPreviousMessages : UiEvent
    }

    sealed interface UiNews {
        data object ScrollToBottom : UiNews
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
    }

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): ChatComponent
    }
}
