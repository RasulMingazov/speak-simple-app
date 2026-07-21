package org.speaksimpleapp.feature.root

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.feature.auth.presentation.LoginComponent
import org.speaksimpleapp.feature.chat.presentation.ChatComponent

interface RootComponent {
    val uiState: StateFlow<UiState>
    val loginComponent: LoginComponent
    val chatComponent: ChatComponent

    enum class UiState { INITIALIZING, LOGIN, CHAT }

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): RootComponent
    }
}
