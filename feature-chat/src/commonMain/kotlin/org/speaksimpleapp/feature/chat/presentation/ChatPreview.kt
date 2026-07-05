package org.speaksimpleapp.feature.chat.presentation

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatRole

@Preview
@Composable
private fun ChatScreenLightPreview() {
    PreviewTheme(darkTheme = false) {
        ChatScreen(
            state = previewState(),
            onMessageChanged = {},
            onSendClicked = {},
            onLoadOlderMessages = {}
        )
    }
}

@Preview
@Composable
private fun ChatScreenDarkPreview() {
    PreviewTheme(darkTheme = true) {
        ChatScreen(
            state = previewState(),
            onMessageChanged = {},
            onSendClicked = {},
            onLoadOlderMessages = {}
        )
    }
}

private fun previewState(): ChatComponent.UiState =
    ChatComponent.UiState(
        title = "SpeakSimple Chat",
        subtitle = "Text practice now. Voice messages come next.",
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
        isLoadingOlder = false,
        hasMoreOlder = true,
        isSending = false,
        scrollToBottomRequest = 0
    )

@Composable
private fun PreviewTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            darkColorScheme(background = Color(0xFF111318))
        } else {
            lightColorScheme(background = Color(0xFFFBFCFE))
        },
        content = content
    )
}
