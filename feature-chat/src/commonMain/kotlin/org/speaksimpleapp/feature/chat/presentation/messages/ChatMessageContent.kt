package org.speaksimpleapp.feature.chat.presentation.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatRole
import speak_simple_app.feature_chat.generated.resources.Res
import speak_simple_app.feature_chat.generated.resources.chat_loading_previous_messages

@Composable
internal fun ChatMessageContent(
    state: ChatMessagesComponent.UiState,
    news: Flow<ChatMessagesComponent.News>,
    listState: LazyListState,
    onLoadPreviousMessages: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ChatMessageEffects(
        news = news,
        state = state,
        listState = listState,
        onLoadPreviousMessages = onLoadPreviousMessages
    )

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(top = 2.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (state.isPreviousLoading) {
            item(key = "loading-previous") {
                LoadingPreviousMessages()
            }
        }

        items(
            items = state.messages,
            key = ChatMessage::id
        ) { message ->
            MessageBubble(message = message)
        }
    }
}

@Composable
private fun ChatMessageEffects(
    news: Flow<ChatMessagesComponent.News>,
    state: ChatMessagesComponent.UiState,
    listState: LazyListState,
    onLoadPreviousMessages: (String) -> Unit
) {
    ScrollToBottomEffect(
        news = news,
        state = state,
        listState = listState
    )
    LoadPreviousMessagesEffect(
        state = state,
        listState = listState,
        onLoadPreviousMessages = onLoadPreviousMessages
    )
}

@Composable
private fun ScrollToBottomEffect(
    news: Flow<ChatMessagesComponent.News>,
    state: ChatMessagesComponent.UiState,
    listState: LazyListState
) {
    val currentState by rememberUpdatedState(state)

    LaunchedEffect(news) {
        news
            .filter { it == ChatMessagesComponent.News.ScrollToBottom }
            .collect {
                withFrameNanos { }
                val lastIndex = getLastContentIndex(currentState)

                if (lastIndex >= 0) {
                    listState.animateScrollToItem(lastIndex)
                }
            }
    }
}

@Composable
private fun LoadPreviousMessagesEffect(
    state: ChatMessagesComponent.UiState,
    listState: LazyListState,
    onLoadPreviousMessages: (String) -> Unit
) {
    val currentState by rememberUpdatedState(state)

    LaunchedEffect(listState, state.previousPageKey) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .filter { firstVisibleItemIndex ->
                currentState.canLoadPrevious && isNearFirstMessage(firstVisibleItemIndex)
            }
            .distinctUntilChanged()
            .collect {
                currentState.previousPageKey?.let(onLoadPreviousMessages)
            }
    }
}

@Composable
private fun LoadingPreviousMessages() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.chat_loading_previous_messages),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor = when (message.role) {
        ChatRole.User -> colorScheme.primaryContainer
        ChatRole.Feedback -> colorScheme.secondaryContainer
        ChatRole.Assistant -> colorScheme.surfaceVariant
    }
    val borderColor = when (message.role) {
        ChatRole.User -> colorScheme.primary.copy(alpha = 0.26f)
        ChatRole.Feedback -> colorScheme.secondary.copy(alpha = 0.42f)
        ChatRole.Assistant -> colorScheme.outline.copy(alpha = 0.46f)
    }
    val textColor = when (message.role) {
        ChatRole.User, ChatRole.Feedback -> colorScheme.onBackground
        ChatRole.Assistant -> colorScheme.onSurface
    }
    val isUser = message.role == ChatRole.User
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        val bubbleShape = RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = if (isUser) 18.dp else 6.dp,
            bottomEnd = if (isUser) 6.dp else 18.dp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.86f)
                .clip(bubbleShape)
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = bubbleShape
                )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}

private fun getLastContentIndex(state: ChatMessagesComponent.UiState): Int {
    val loadingItemsCount = if (state.isPreviousLoading) 1 else 0
    return loadingItemsCount + state.messages.size - 1
}

private fun isNearFirstMessage(firstVisibleItemIndex: Int): Boolean =
    firstVisibleItemIndex <= 1
