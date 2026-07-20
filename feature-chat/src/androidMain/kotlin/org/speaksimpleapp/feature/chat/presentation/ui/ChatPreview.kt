package org.speaksimpleapp.feature.chat.presentation.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.core.design.theme.SpeakSimpleTheme
import org.speaksimpleapp.feature.chat.domain.model.ChatId
import org.speaksimpleapp.feature.chat.domain.model.MessageSendingAvailability
import org.speaksimpleapp.feature.chat.presentation.ChatContent
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputComponent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ChatContentPreview() {
    SpeakSimpleTheme {
        ChatContent(
            messagesComponent = PreviewMessagesComponent,
            inputComponent = PreviewInputComponent,
        )
    }
}

private fun previewMessagesState(): ChatMessagesComponent.UiState =
    ChatMessagesComponent.UiState(
        chatId = PreviewChatId,
        title = "Weekend plans",
        messageItems = listOf(
            previewMessage(
                id = "1",
                type = ChatMessagesComponent.MessageType.Assistant,
                text = "You just finished a team call. Try asking a colleague how their week is going.",
            ),
            previewMessage(
                id = "2",
                type = ChatMessagesComponent.MessageType.User,
                text = "How is your week going so far?",
                suggestionCount = 2,
            ),
            previewMessage(
                id = "3",
                type = ChatMessagesComponent.MessageType.Assistant,
                text = "Pretty good, thanks. It’s been a busy week, but I finally wrapped up a project. How about you?",
            ),
            previewMessage(
                id = "4",
                type = ChatMessagesComponent.MessageType.User,
                text = "It has been busy, but I’m learning a lot from the new project.",
                suggestionCount = 1,
            ),
            previewMessage(
                id = "5",
                type = ChatMessagesComponent.MessageType.Assistant,
                text = "That sounds productive. What part of the new project has been the most interesting so far?",
            ),
        ),
        assistantTypingKey = null,
        sendingAvailability = MessageSendingAvailability.Available(messagesBeforeWarning = 98),
        isInitialLoading = false,
    )

private fun previewInputState(): ChatInputComponent.UiState =
    ChatInputComponent.UiState(
        message = "",
        isSending = false,
        canSend = false,
        isLimitReached = false,
    )

private fun previewMessage(
    id: String,
    type: ChatMessagesComponent.MessageType,
    text: String,
    suggestionCount: Int = 0,
): ChatMessagesComponent.MessageItem = ChatMessagesComponent.MessageItem(
    key = id,
    text = text,
    type = type,
    suggestionCount = suggestionCount,
    animateAppearance = false,
)

private val PreviewChatId = ChatId("preview-chat")

private object PreviewMessagesComponent : ChatMessagesComponent {
    override val uiState: StateFlow<ChatMessagesComponent.UiState> =
        MutableStateFlow(previewMessagesState())
    override val news: Flow<ChatMessagesComponent.News> = emptyFlow()
}

private object PreviewInputComponent : ChatInputComponent {
    override val uiState: StateFlow<ChatInputComponent.UiState> =
        MutableStateFlow(previewInputState())

    override fun dispatch(event: ChatInputComponent.Event) = Unit
}
