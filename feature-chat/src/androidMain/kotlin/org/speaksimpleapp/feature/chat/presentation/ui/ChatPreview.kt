package org.speaksimpleapp.feature.chat.presentation.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.emptyFlow
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatRole
import org.speaksimpleapp.feature.chat.presentation.ChatComponent

@Preview
@Composable
private fun ChatScreenLightPreview() {
    PreviewTheme(darkTheme = false) {
        ChatScreen(
            state = previewState(),
            news = emptyFlow(),
            onMessageChanged = {},
            onSendClicked = {},
            onLoadPreviousMessages = {}
        )
    }
}

@Preview
@Composable
private fun ChatScreenDarkPreview() {
    PreviewTheme(darkTheme = true) {
        ChatScreen(
            state = previewState(),
            news = emptyFlow(),
            onMessageChanged = {},
            onSendClicked = {},
            onLoadPreviousMessages = {}
        )
    }
}

private fun previewState(): ChatComponent.UiState =
    ChatComponent.UiState(
        inputMessage = "Can you help me?",
        messages = listOf(
            ChatMessage(
                id = "1",
                role = ChatRole.Assistant,
                text = "Hi! Send me a message in English, and I will help you make it sound more natural."
            ),
            ChatMessage(
                id = "2",
                role = ChatRole.User,
                text = "I want improve my speaking"
            ),
            ChatMessage(
                id = "3",
                role = ChatRole.Assistant,
                text = "Got it. You can say this more naturally by adding a verb form and a little context."
            )
        ),
        feedback = ChatFeedback(
            improvedText = "I want to improve my speaking skills.",
            explanation = "Use \"want to\" before a verb and add a noun like \"skills\" to make the sentence complete.",
            suggestions = listOf(
                "Add \"to\" after want.",
                "Say what skill you want to improve.",
                "Add context if you can."
            ),
            constructions = listOf(
                "I want to improve...",
                "I am trying to...",
                "Could you help me with..."
            )
        ),
        isInitialLoading = false,
        isPreviousLoading = false,
        hasMorePrevious = true,
        isSending = false
    )

@Composable
private fun PreviewTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkPreviewColors() else lightPreviewColors(),
        content = content
    )
}

private fun lightPreviewColors() = lightColorScheme(
    primary = Color(0xFF7652D6),
    onPrimary = Color.White,
    secondary = Color(0xFF967A00),
    background = Color(0xFFFCFBFF),
    onBackground = Color(0xFF181521),
    surface = Color.White,
    onSurface = Color(0xFF181521),
    surfaceVariant = Color(0xFFF0EDF7),
    onSurfaceVariant = Color(0xFF625B70),
    primaryContainer = Color(0xFFE5D9FF),
    secondaryContainer = Color(0xFFFFEDB7),
    outline = Color(0xFFE0DAEA)
)

private fun darkPreviewColors() = darkColorScheme(
    primary = Color(0xFFBFA2FF),
    onPrimary = Color(0xFF201632),
    secondary = Color(0xFFFFD66B),
    background = Color(0xFF111116),
    onBackground = Color(0xFFF6F3FB),
    surface = Color(0xFF17171E),
    onSurface = Color(0xFFF6F3FB),
    surfaceVariant = Color(0xFF25242C),
    onSurfaceVariant = Color(0xFFB9B4C2),
    primaryContainer = Color(0xFF3B2A59),
    secondaryContainer = Color(0xFF37301D),
    outline = Color(0xFF34323D)
)
