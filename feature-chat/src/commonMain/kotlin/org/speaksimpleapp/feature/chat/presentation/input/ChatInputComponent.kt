package org.speaksimpleapp.feature.chat.presentation.input

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.feature.chat.domain.model.ChatId
import org.speaksimpleapp.feature.chat.domain.model.MessageSendingAvailability

interface ChatInputComponent {
    val uiState: StateFlow<UiState>

    fun dispatch(event: Event)

    sealed interface Event {
        data class MessageChanged(val message: String) : Event
        data class ChatChanged(
            val chatId: ChatId,
            val sendingAvailability: MessageSendingAvailability,
        ) : Event
        data object SendClicked : Event
    }

    data class UiState(
        val message: String,
        val isSending: Boolean,
        val canSend: Boolean,
        val isLimitReached: Boolean,
    )

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): ChatInputComponent
    }
}
