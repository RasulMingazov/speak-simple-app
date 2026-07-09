package org.speaksimpleapp.feature.chat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputComponent
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputContent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessageContent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent
import speak_simple_app.feature_chat.generated.resources.Res
import speak_simple_app.feature_chat.generated.resources.chat_avatar_text
import speak_simple_app.feature_chat.generated.resources.chat_subtitle
import speak_simple_app.feature_chat.generated.resources.chat_title

@Composable
fun ChatContent(
    component: ChatComponent,
    modifier: Modifier = Modifier
) {
    val messagesState by component.messages.uiState.collectAsState()
    val inputState by component.input.uiState.collectAsState()

    ChatContent(
        messagesState = messagesState,
        inputState = inputState,
        news = component.messages.news,
        onMessageChanged = {
            component.input.dispatch(ChatInputComponent.Event.MessageChanged(it))
        },
        onSendClicked = {
            component.input.dispatch(ChatInputComponent.Event.SendClicked)
        },
        onLoadPreviousMessages = { beforeMessageId ->
            component.messages.dispatch(
                ChatMessagesComponent.Event.LoadPreviousMessages(
                    beforeMessageId = beforeMessageId
                )
            )
        },
        modifier = modifier
    )
}

@Composable
internal fun ChatContent(
    messagesState: ChatMessagesComponent.UiState,
    inputState: ChatInputComponent.UiState,
    news: Flow<ChatMessagesComponent.News>,
    onMessageChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onLoadPreviousMessages: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(chatBackgroundBrush())
            .safeDrawingPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp)
        ) {
            ChatHeader()
            Spacer(modifier = Modifier.height(12.dp))
            ChatMessageContent(
                state = messagesState,
                news = news,
                listState = listState,
                onLoadPreviousMessages = onLoadPreviousMessages,
                modifier = Modifier.weight(1f)
            )
        }

        ChatInputContent(
            text = inputState.message,
            canSend = inputState.canSend,
            isSending = inputState.isSending,
            onTextChanged = onMessageChanged,
            onSendClicked = onSendClicked,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )
    }
}

@Composable
private fun chatBackgroundBrush(): Brush {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background.luminance() < 0.5f

    return if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                colorScheme.background,
                colorScheme.background
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                colorScheme.background,
                colorScheme.surface,
                colorScheme.secondaryContainer.copy(alpha = 0.28f)
            )
        )
    }
}

@Composable
private fun ChatHeader() {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.chat_avatar_text),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.chat_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onBackground
            )
            Text(
                text = stringResource(Res.string.chat_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}
