package org.speaksimpleapp.feature.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatRole
import org.speaksimpleapp.feature.chat.presentation.ChatComponent
import speak_simple_app.feature_chat.generated.resources.Res
import speak_simple_app.feature_chat.generated.resources.chat_avatar_text
import speak_simple_app.feature_chat.generated.resources.chat_coach_note
import speak_simple_app.feature_chat.generated.resources.chat_loading_previous_messages
import speak_simple_app.feature_chat.generated.resources.chat_message_placeholder
import speak_simple_app.feature_chat.generated.resources.chat_more_natural
import speak_simple_app.feature_chat.generated.resources.chat_phrases
import speak_simple_app.feature_chat.generated.resources.chat_send
import speak_simple_app.feature_chat.generated.resources.chat_sending
import speak_simple_app.feature_chat.generated.resources.chat_subtitle
import speak_simple_app.feature_chat.generated.resources.chat_title
import speak_simple_app.feature_chat.generated.resources.chat_try
import speak_simple_app.feature_chat.generated.resources.chat_why

@Composable
fun ChatContent(
    component: ChatComponent,
    modifier: Modifier = Modifier
) {
    val state by component.uiState.collectAsState()

    ChatScreen(
        state = state,
        news = component.news,
        onMessageChanged = {
            component.dispatch(ChatComponent.Event.MessageChanged(it))
        },
        onSendClicked = {
            component.dispatch(ChatComponent.Event.SendClicked)
        },
        onLoadPreviousMessages = {
            component.dispatch(ChatComponent.Event.LoadPreviousMessages)
        },
        modifier = modifier
    )
}

@Composable
internal fun ChatScreen(
    state: ChatComponent.UiState,
    news: Flow<ChatComponent.News>,
    onMessageChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onLoadPreviousMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val colorScheme = MaterialTheme.colorScheme

    ChatListEffects(
        news = news,
        state = state,
        listState = listState,
        onLoadPreviousMessages = onLoadPreviousMessages
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.background,
                        colorScheme.surface,
                        colorScheme.secondaryContainer.copy(alpha = 0.28f)
                    )
                )
            )
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
            ChatMessageList(
                uiState = state,
                listState = listState,
                modifier = Modifier.weight(1f)
            )
        }

        ChatComposer(
            text = state.inputMessage,
            canSend = state.canSend,
            isSending = state.isSending,
            onTextChanged = onMessageChanged,
            onSendClicked = onSendClicked,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )
    }
}

@Composable
private fun ChatMessageList(
    uiState: ChatComponent.UiState,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(top = 2.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (uiState.isPreviousLoading) {
            item(key = "loading-previous") {
                LoadingPreviousMessages()
            }
        }

        items(
            items = uiState.messages,
            key = ChatMessage::id
        ) { message ->
            ChatMessageBubble(message = message)
        }

        uiState.feedback?.let { feedback ->
            item(key = "feedback") {
                FeedbackCard(feedback = feedback)
            }
        }
    }
}

@Composable
private fun ChatListEffects(
    news: Flow<ChatComponent.News>,
    state: ChatComponent.UiState,
    listState: LazyListState,
    onLoadPreviousMessages: () -> Unit
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
    news: Flow<ChatComponent.News>,
    state: ChatComponent.UiState,
    listState: LazyListState
) {
    val currentState by rememberUpdatedState(state)

    LaunchedEffect(news) {
        news
            .filter { it == ChatComponent.News.ScrollToBottom }
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
    state: ChatComponent.UiState,
    listState: LazyListState,
    onLoadPreviousMessages: () -> Unit
) {
    val currentState by rememberUpdatedState(state)

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .filter { firstVisibleItemIndex ->
                currentState.canLoadPrevious && isNearFirstMessage(firstVisibleItemIndex)
            }
            .distinctUntilChanged()
            .collect { onLoadPreviousMessages() }
    }
}

private fun getLastContentIndex(state: ChatComponent.UiState): Int {
    val loadingItemsCount = if (state.isPreviousLoading) 1 else 0
    val feedbackItemsCount = if (state.feedback == null) 0 else 1
    return loadingItemsCount + state.messages.size + feedbackItemsCount - 1
}

private fun isNearFirstMessage(firstVisibleItemIndex: Int): Boolean =
    firstVisibleItemIndex <= 1

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

@Composable
private fun ChatMessageBubble(message: ChatMessage) {
    val isUser = message.role == ChatRole.User
    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor = if (isUser) colorScheme.primaryContainer else colorScheme.surfaceVariant
    val borderColor = if (isUser) {
        colorScheme.primary.copy(alpha = 0.26f)
    } else {
        colorScheme.outline.copy(alpha = 0.46f)
    }
    val textColor = if (isUser) colorScheme.onBackground else colorScheme.onSurfaceVariant

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

@Composable
private fun FeedbackCard(feedback: ChatFeedback) {
    val colorScheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colorScheme.secondaryContainer)
            .border(
                width = 1.dp,
                color = colorScheme.secondary.copy(alpha = 0.42f),
                shape = shape
            )
    ) {
        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(colorScheme.secondary)
                )
                Text(
                    text = stringResource(Res.string.chat_coach_note),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onBackground
                )
            }
            FeedbackSection(
                title = stringResource(Res.string.chat_more_natural),
                body = feedback.improvedText
            )
            FeedbackSection(
                title = stringResource(Res.string.chat_why),
                body = feedback.explanation
            )
            HorizontalDivider(color = colorScheme.onBackground.copy(alpha = 0.12f))
            CompactList(
                title = stringResource(Res.string.chat_try),
                items = feedback.suggestions.take(2)
            )
            CompactList(
                title = stringResource(Res.string.chat_phrases),
                items = feedback.constructions.take(3)
            )
        }
    }
}

@Composable
private fun FeedbackSection(
    title: String,
    body: String
) {
    val textColor = MaterialTheme.colorScheme.onBackground
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = textColor.copy(alpha = 0.82f)
        )
    }
}

@Composable
private fun CompactList(
    title: String,
    items: List<String>
) {
    val textColor = MaterialTheme.colorScheme.onBackground
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
        items.forEach { item ->
            Text(
                text = "- $item",
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.82f)
            )
        }
    }
}

@Composable
private fun ChatComposer(
    text: String,
    canSend: Boolean,
    isSending: Boolean,
    onTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val inputShape = RoundedCornerShape(28.dp)
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(inputShape)
                .background(colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = colorScheme.outline,
                    shape = inputShape
                )
                .padding(start = 18.dp, top = 7.dp, end = 14.dp, bottom = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChanged,
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = 48.dp),
                enabled = !isSending,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = colorScheme.onBackground
                ),
                cursorBrush = SolidColor(colorScheme.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (canSend) onSendClicked()
                    }
                ),
                minLines = 1,
                maxLines = 4,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.chat_message_placeholder),
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.62f)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
        SendAction(
            enabled = canSend,
            isSending = isSending,
            onClick = onSendClicked
        )
    }
}

@Composable
private fun SendAction(
    enabled: Boolean,
    isSending: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val containerColor = if (enabled) colorScheme.primary else colorScheme.surfaceVariant
    val contentColor = if (enabled) colorScheme.onPrimary else colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isSending) {
                stringResource(Res.string.chat_sending)
            } else {
                stringResource(Res.string.chat_send)
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}
