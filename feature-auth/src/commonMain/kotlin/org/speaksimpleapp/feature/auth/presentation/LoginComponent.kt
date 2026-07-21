package org.speaksimpleapp.feature.auth.presentation

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource

interface LoginComponent {
    val uiState: StateFlow<LoginUiState>

    fun dispatch(event: Event)

    sealed interface Event {
        data object GoogleLoginClicked : Event
        data object ErrorDismissed : Event
    }

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): LoginComponent
    }
}

data class LoginUiState(
    val brandMark: StringResource,
    val title: StringResource,
    val subtitle: StringResource,
    val googleButtonTitle: StringResource,
    val googleIconContentDescription: StringResource,
    val privacyMessage: StringResource,
    val isGoogleSignInInProgress: Boolean,
    val error: LoginErrorUiState?,
)

data class LoginErrorUiState(
    val title: StringResource,
    val message: StringResource,
    val iconContentDescription: StringResource,
)
