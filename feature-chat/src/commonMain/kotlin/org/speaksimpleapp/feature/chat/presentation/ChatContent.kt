package org.speaksimpleapp.feature.chat.presentation

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
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatRole
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
    val state by component.uiState.subscribeAsState()

    ChatScreen(
        state = state,
        news = component.news,
        onMessageChanged = {
            component.handle(ChatComponent.UiEvent.MessageChanged(it))
        },
        onSendClicked = {
            component.handle(ChatComponent.UiEvent.SendClicked)
        },
        onLoadPreviousMessages = {
            component.handle(ChatComponent.UiEvent.LoadPreviousMessages)
        },
        modifier = modifier
    )
}

@Composable
internal fun ChatScreen(
    state: ChatComponent.UiState,
    news: Flow<ChatComponent.UiNews>,
    onMessageChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onLoadPreviousMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val palette = chatPalette()
    val currentState by rememberUpdatedState(state)

    LaunchedEffect(news) {
        news.collect { uiNews ->
            when (uiNews) {
                ChatComponent.UiNews.ScrollToBottom -> {
                    withFrameNanos { }
                    val topItemsCount = if (currentState.isPreviousLoading) 1 else 0
                    val lastContentIndex =
                        topItemsCount + currentState.messages.size + if (currentState.feedback != null) 1 else 0 - 1
                    if (lastContentIndex >= 0) {
                        listState.animateScrollToItem(lastContentIndex)
                    }
                }
            }
        }
    }

    LaunchedEffect(listState, state.hasMorePrevious, state.isPreviousLoading, state.isInitialLoading) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { firstVisibleItemIndex ->
                if (
                    firstVisibleItemIndex <= 1 &&
                    state.hasMorePrevious &&
                    !state.isPreviousLoading &&
                    !state.isInitialLoading
                ) {
                    onLoadPreviousMessages()
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        palette.backgroundTop,
                        palette.backgroundMid,
                        palette.backgroundBottom
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
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
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
                    ChatMessageBubble(message = message)
                }

                state.feedback?.let { feedback ->
                    item(key = "feedback") {
                        FeedbackCard(feedback = feedback)
                    }
                }
            }
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
private fun LoadingPreviousMessages() {
    val palette = chatPalette()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.chat_loading_previous_messages),
            style = MaterialTheme.typography.labelSmall,
            color = palette.secondaryText
        )
    }
}

@Composable
private fun ChatHeader() {
    val palette = chatPalette()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(palette.avatarBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.chat_avatar_text),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = palette.avatarText
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.chat_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = palette.primaryText
            )
            Text(
                text = stringResource(Res.string.chat_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = palette.secondaryText
            )
        }
    }
}

@Composable
private fun ChatMessageBubble(message: ChatMessage) {
    val isUser = message.role == ChatRole.User
    val palette = chatPalette()
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
                .background(if (isUser) palette.userBubble else palette.assistantBubble)
                .border(
                    width = 1.dp,
                    color = if (isUser) palette.userBubbleBorder else palette.assistantBubbleBorder,
                    shape = bubbleShape
                )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) palette.userText else palette.assistantText
            )
        }
    }
}

@Composable
private fun FeedbackCard(feedback: ChatFeedback) {
    val palette = chatPalette()
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.coachBackground)
            .border(
                width = 1.dp,
                color = palette.coachBorder,
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
                        .background(palette.coachAccent)
                )
                Text(
                    text = stringResource(Res.string.chat_coach_note),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.coachText
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
            HorizontalDivider(color = palette.coachText.copy(alpha = 0.12f))
            CompactList(title = stringResource(Res.string.chat_try), items = feedback.suggestions.take(2))
            CompactList(title = stringResource(Res.string.chat_phrases), items = feedback.constructions.take(3))
        }
    }
}

@Composable
private fun FeedbackSection(
    title: String,
    body: String
) {
    val palette = chatPalette()
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = palette.coachText
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = palette.coachText.copy(alpha = 0.82f)
        )
    }
}

@Composable
private fun CompactList(
    title: String,
    items: List<String>
) {
    val palette = chatPalette()
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = palette.coachText
        )
        items.forEach { item ->
            Text(
                text = "- $item",
                style = MaterialTheme.typography.bodySmall,
                color = palette.coachText.copy(alpha = 0.82f)
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
    val palette = chatPalette()
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
                .background(palette.composerBackground)
                .border(
                    width = 1.dp,
                    color = palette.composerBorder,
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
                    color = palette.primaryText
                ),
                cursorBrush = SolidColor(palette.accent),
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
                                color = palette.secondaryText.copy(alpha = 0.62f)
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
    val palette = chatPalette()
    val containerColor = if (enabled) palette.accent else palette.composerActionBackground
    val contentColor = if (enabled) palette.onAccent else palette.secondaryText

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
