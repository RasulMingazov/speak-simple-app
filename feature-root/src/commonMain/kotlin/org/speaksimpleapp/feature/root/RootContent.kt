package org.speaksimpleapp.feature.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.speaksimpleapp.feature.auth.presentation.LoginContent
import org.speaksimpleapp.feature.chat.presentation.ChatContent

@Composable
fun RootContent(component: RootComponent) {
    val state by component.uiState.collectAsState()
    when (state) {
        RootComponent.UiState.INITIALIZING -> Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        RootComponent.UiState.LOGIN -> LoginContent(component.loginComponent)
        RootComponent.UiState.CHAT -> ChatContent(component.chatComponent)
    }
}
