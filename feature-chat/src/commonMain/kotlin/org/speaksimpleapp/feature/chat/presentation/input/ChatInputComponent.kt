package org.speaksimpleapp.feature.chat.presentation.input

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface ChatInputComponent {
    val uiState: StateFlow<UiState>

    fun dispatch(event: Event)

    sealed interface Event {
        data class MessageChanged(val message: String) : Event
        data object SendClicked : Event
    }

    data class UiState(
        val message: String,
        val isSending: Boolean,
        val canSend: Boolean
    )

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): ChatInputComponent
    }
}
