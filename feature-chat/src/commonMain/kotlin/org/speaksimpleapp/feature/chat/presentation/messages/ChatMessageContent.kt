package org.speaksimpleapp.feature.chat.presentation.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import speak_simple_app.feature_chat.generated.resources.Res
import speak_simple_app.feature_chat.generated.resources.chat_play
import speak_simple_app.feature_chat.generated.resources.chat_suggestions
import speak_simple_app.feature_chat.generated.resources.chat_translate

@Composable
internal fun ChatMessageContent(
    component: ChatMessagesComponent,
    isAssistantTyping: Boolean,
    modifier: Modifier = Modifier,
) {
    val state by component.uiState.collectAsState()
    val animatedMessageKeys = remember { mutableSetOf<String>() }

    ChatMessageContent(
        state = state,
        isAssistantTyping = isAssistantTyping,
        news = component.news,
        listState = rememberLazyListState(),
        animatedMessageKeys = animatedMessageKeys,
        modifier = modifier,
    )
}

@Composable
private fun ChatMessageContent(
    state: ChatMessagesComponent.UiState,
    isAssistantTyping: Boolean,
    news: Flow<ChatMessagesComponent.News>,
    listState: LazyListState,
    animatedMessageKeys: MutableSet<String>,
    modifier: Modifier = Modifier,
) {
    val items = visibleItems(
        messageItems = state.messageItems,
        assistantTypingKey = state.assistantTypingKey,
        isAssistantTyping = isAssistantTyping,
    )

    ScrollToBottomEffect(
        news = news,
        itemCount = items.size,
        listState = listState,
    )

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = messagesContentPadding(),
        verticalArrangement = Arrangement.spacedBy(MessagesSpacing),
    ) {
        items(
            items = items,
            key = ChatListItem::key,
        ) { item ->
            when (item) {
                is ChatListItem.Message -> MessageBubble(
                    item = item.item,
                    animatedMessageKeys = animatedMessageKeys,
                )

                is ChatListItem.AssistantTyping -> AssistantTypingIndicator()
            }
        }
    }
}

@Composable
private fun MessageBubble(
    item: ChatMessagesComponent.MessageItem,
    animatedMessageKeys: MutableSet<String>,
) {
    val colorScheme = MaterialTheme.colorScheme
    val style = messageBubbleStyle(
        type = item.type,
        colorScheme = colorScheme,
        isDark = colorScheme.background.luminance() < 0.5f,
    )
    val shouldAnimate = remember(item.key) {
        item.animateAppearance && item.key !in animatedMessageKeys
    }

    LaunchedEffect(item.key) {
        animatedMessageKeys += item.key
    }

    if (!shouldAnimate) {
        MessageBubbleLayout(
            item = item,
            style = style,
        )
        return
    }

    val visibilityState = remember {
        MutableTransitionState(initialState = false).apply {
            targetState = true
        }
    }
    val isUserMessage = item.type == ChatMessagesComponent.MessageType.User
    AnimatedVisibility(
        visibleState = visibilityState,
        enter = fadeIn(animationSpec = tween(durationMillis = 220)) +
            scaleIn(
                animationSpec = tween(durationMillis = 220),
                initialScale = 0.78f,
                transformOrigin = TransformOrigin(
                    pivotFractionX = if (isUserMessage) 1f else 0f,
                    pivotFractionY = 1f,
                ),
            ),
    ) {
        MessageBubbleLayout(
            item = item,
            style = style,
        )
    }
}

@Composable
private fun MessageBubbleLayout(
    item: ChatMessagesComponent.MessageItem,
    style: MessageBubbleStyle,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = style.alignment,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = MessageBubbleMaxWidth)
                .clip(style.shape)
                .background(style.background)
                .border(
                    width = 1.dp,
                    color = style.borderColor,
                    shape = style.shape,
                ),
        ) {
            Text(
                text = item.text,
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 12.dp,
                ),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                ),
                color = style.contentColor,
            )
        }

        when (item.type) {
            ChatMessagesComponent.MessageType.User -> if (item.suggestionCount > 0) {
                SuggestionChip(count = item.suggestionCount)
            }

            ChatMessagesComponent.MessageType.Assistant -> AssistantMessageActions()
            ChatMessagesComponent.MessageType.System -> Unit
        }
    }
}

@Composable
private fun AssistantTypingIndicator() {
    val colorScheme = MaterialTheme.colorScheme
    val style = messageBubbleStyle(
        type = ChatMessagesComponent.MessageType.Assistant,
        colorScheme = colorScheme,
        isDark = colorScheme.background.luminance() < 0.5f,
    )
    val transition = rememberInfiniteTransition(label = "assistantTyping")

    Row(
        modifier = Modifier
            .widthIn(max = 64.dp)
            .clip(style.shape)
            .background(style.background)
            .border(
                width = 1.dp,
                color = style.borderColor,
                shape = style.shape,
            )
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val alpha by transition.animateFloat(
                initialValue = 0.28f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 420,
                        delayMillis = index * 120,
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "typingDot$index",
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = alpha)),
            )
        }
    }
}

@Composable
private fun AssistantMessageActions() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MessageActionChip(
            icon = Icons.Filled.PlayArrow,
            text = stringResource(Res.string.chat_play),
        )
        MessageActionChip(
            icon = Icons.Filled.Language,
            text = stringResource(Res.string.chat_translate),
        )
    }
}

@Composable
private fun SuggestionChip(count: Int) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background.luminance() < 0.5f

    MessageActionChip(
        text = pluralStringResource(
            resource = Res.plurals.chat_suggestions,
            quantity = count,
            count,
        ),
        style = MessageActionChipStyle(
            contentColor = if (isDark) Color(0xFFC895FF) else Color(0xFF6D32AD),
            backgroundColor = colorScheme.primary.copy(alpha = if (isDark) 0.08f else 0.04f),
            borderColor = colorScheme.primary.copy(alpha = 0.28f),
            fontWeight = FontWeight.Medium,
        ),
    )
}

@Composable
private fun MessageActionChip(
    text: String,
    icon: ImageVector? = null,
    style: MessageActionChipStyle = tertiaryChipStyle(MaterialTheme.colorScheme),
) {
    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(MessageActionChipShape)
            .background(style.backgroundColor)
            .border(
                width = 1.dp,
                color = style.borderColor,
                shape = MessageActionChipShape,
            )
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = style.contentColor,
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp,
            ),
            fontWeight = style.fontWeight,
            color = style.contentColor,
        )
    }
}

@Composable
private fun ScrollToBottomEffect(
    news: Flow<ChatMessagesComponent.News>,
    itemCount: Int,
    listState: LazyListState,
) {
    val currentItemCount by rememberUpdatedState(itemCount)

    LaunchedEffect(news) {
        news
            .filter { it == ChatMessagesComponent.News.ScrollToBottom }
            .collect {
                withFrameNanos { }
                if (currentItemCount > 0) listState.animateScrollToItem(currentItemCount - 1)
            }
    }
}

private sealed interface ChatListItem {
    val key: String

    data class Message(
        val item: ChatMessagesComponent.MessageItem,
    ) : ChatListItem {
        override val key: String = item.key
    }

    data class AssistantTyping(
        override val key: String,
    ) : ChatListItem
}

private data class MessageBubbleStyle(
    val alignment: Alignment.Horizontal,
    val shape: RoundedCornerShape,
    val background: Brush,
    val borderColor: Color,
    val contentColor: Color,
)

private data class MessageActionChipStyle(
    val contentColor: Color,
    val backgroundColor: Color,
    val borderColor: Color,
    val fontWeight: FontWeight,
)

private val UserMessageShape = RoundedCornerShape(
    topStart = 22.dp,
    topEnd = 22.dp,
    bottomStart = 22.dp,
    bottomEnd = 8.dp,
)
private val AssistantMessageShape = RoundedCornerShape(
    topStart = 22.dp,
    topEnd = 22.dp,
    bottomStart = 8.dp,
    bottomEnd = 22.dp,
)
private val MessageActionChipShape = RoundedCornerShape(18.dp)

private val MessagesTopPadding = 76.dp
private val MessagesBottomPadding = 92.dp
private val MessagesSpacing = 22.dp
private val MessageBubbleMaxWidth = 320.dp

@Composable
private fun messagesContentPadding(): PaddingValues = PaddingValues(
    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + MessagesTopPadding,
    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + MessagesBottomPadding,
)

private fun visibleItems(
    messageItems: List<ChatMessagesComponent.MessageItem>,
    assistantTypingKey: String?,
    isAssistantTyping: Boolean,
): List<ChatListItem> = buildList {
    messageItems.forEach { item ->
        add(ChatListItem.Message(item = item))
    }

    if (isAssistantTyping && assistantTypingKey != null) {
        add(ChatListItem.AssistantTyping(key = assistantTypingKey))
    }
}

private fun messageBubbleStyle(
    type: ChatMessagesComponent.MessageType,
    colorScheme: ColorScheme,
    isDark: Boolean,
): MessageBubbleStyle = when (type) {
    ChatMessagesComponent.MessageType.User -> MessageBubbleStyle(
        alignment = Alignment.End,
        shape = UserMessageShape,
        background = Brush.linearGradient(
            colors = if (isDark) {
                listOf(Color(0xFF9A6CCB), Color(0xFF7A4EA3))
            } else {
                listOf(Color(0xFFAA78E4), Color(0xFF9255D0))
            },
        ),
        borderColor = colorScheme.primary.copy(alpha = 0.38f),
        contentColor = Color(0xFFFFFAFC),
    )

    ChatMessagesComponent.MessageType.Assistant,
    ChatMessagesComponent.MessageType.System -> MessageBubbleStyle(
        alignment = Alignment.Start,
        shape = AssistantMessageShape,
        background = SolidColor(if (isDark) Color(0xFF29262F) else Color(0xFFFFFEFF)),
        borderColor = if (isDark) {
            Color(0xFF77717D).copy(alpha = 0.62f)
        } else {
            Color(0xFF8A63B8).copy(alpha = 0.16f)
        },
        contentColor = colorScheme.onSurface,
    )
}

private fun tertiaryChipStyle(colorScheme: ColorScheme): MessageActionChipStyle {
    val isDark = colorScheme.background.luminance() < 0.5f
    return MessageActionChipStyle(
        contentColor = colorScheme.onSurfaceVariant.copy(alpha = 0.86f),
        backgroundColor = Color.White.copy(alpha = if (isDark) 0.022f else 0.72f),
        borderColor = colorScheme.outline.copy(alpha = if (isDark) 0.40f else 0.68f),
        fontWeight = FontWeight.Normal,
    )
}
