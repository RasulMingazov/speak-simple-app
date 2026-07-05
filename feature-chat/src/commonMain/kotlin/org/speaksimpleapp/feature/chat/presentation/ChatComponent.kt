package org.speaksimpleapp.feature.chat.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage

interface ChatComponent {
    val uiState: Value<UiState>

    fun handle(uiEvent: UiEvent)

    sealed interface UiEvent {
        data class MessageChanged(val message: String) : UiEvent
        data object SendClicked : UiEvent
        data object LoadOlderMessages : UiEvent
    }

    data class UiState(
        val title: String,
        val subtitle: String,
        val inputMessage: String,
        val messages: List<ChatMessage>,
        val feedback: ChatFeedback?,
        val isInitialLoading: Boolean,
        val isLoadingOlder: Boolean,
        val hasMoreOlder: Boolean,
        val isSending: Boolean,
        val scrollToBottomRequest: Int
    ) {
        val canSend: Boolean = inputMessage.isNotBlank() && !isSending
    }

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): ChatComponent
    }
}
