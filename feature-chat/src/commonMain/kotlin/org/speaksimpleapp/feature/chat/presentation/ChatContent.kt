package org.speaksimpleapp.feature.chat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import org.jetbrains.compose.resources.stringResource
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputComponent
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputContent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessageContent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent
import speak_simple_app.feature_chat.generated.resources.Res
import speak_simple_app.feature_chat.generated.resources.chat_avatar_text
import speak_simple_app.feature_chat.generated.resources.chat_back
import speak_simple_app.feature_chat.generated.resources.chat_title

@Composable
fun ChatContent(
    component: ChatComponent,
    modifier: Modifier = Modifier
) {
    val messagesComponent = component.messages
    val inputComponent = component.input

    ChatContent(
        messagesComponent = messagesComponent,
        inputComponent = inputComponent,
        modifier = modifier
    )
}

@Composable
internal fun ChatContent(
    messagesComponent: ChatMessagesComponent,
    inputComponent: ChatInputComponent,
    onBackClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val messagesState by messagesComponent.uiState.collectAsState()
    val inputState by inputComponent.uiState.collectAsState()
    val hazeState = remember { HazeState() }
    val title = messagesState.title.ifBlank {
        stringResource(Res.string.chat_title)
    }

    LaunchedEffect(messagesState.chatId, messagesState.sendingAvailability) {
        val chatId = messagesState.chatId ?: return@LaunchedEffect
        val sendingAvailability = messagesState.sendingAvailability ?: return@LaunchedEffect
        inputComponent.dispatch(
            ChatInputComponent.Event.ChatChanged(
                chatId = chatId,
                sendingAvailability = sendingAvailability,
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        ChatMessageContent(
            component = messagesComponent,
            isAssistantTyping = inputState.isSending,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .hazeSource(state = hazeState)
                .padding(horizontal = ContentHorizontalPadding)
        )

        SystemBarsBlur(hazeState = hazeState)

        ChatHeader(
            title = title,
            onBackClicked = onBackClicked,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = ContentHorizontalPadding)
        )

        ChatInputContent(
            component = inputComponent,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(
                    start = ContentHorizontalPadding,
                    end = ContentHorizontalPadding,
                    bottom = 4.dp,
                )
        )
    }
}

@Composable
private fun BoxScope.SystemBarsBlur(hazeState: HazeState) {
    val statusBarHeight = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()
    val navigationBarHeight = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    SystemBarBlur(
        hazeState = hazeState,
        height = statusBarHeight + ChatHeaderHeight / 2 + 8.dp,
        mask = TopSystemBarBlurMask,
        modifier = Modifier.align(Alignment.TopCenter)
    )
    SystemBarBlur(
        hazeState = hazeState,
        height = navigationBarHeight + ChatHeaderHeight / 2,
        mask = BottomSystemBarBlurMask,
        modifier = Modifier.align(Alignment.BottomCenter)
    )
}

@Composable
private fun SystemBarBlur(
    hazeState: HazeState,
    height: Dp,
    mask: Brush,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .hazeEffect(state = hazeState) {
                this.backgroundColor = backgroundColor
                this.blurRadius = 4.dp
                this.mask = mask
            }
    )
}

@Composable
private fun ChatHeader(
    title: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background.luminance() < 0.5f
    val headerShape = RoundedCornerShape(22.dp)
    val headerColor = if (isDark) DarkHeaderColor else LightHeaderColor

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(ChatHeaderHeight)
                .shadow(
                    elevation = FloatingSurfaceElevation,
                    shape = CircleShape,
                    clip = false,
                )
                .clip(CircleShape)
                .background(headerColor)
                .border(
                    width = 1.dp,
                    color = colorScheme.outline.copy(alpha = 0.72f),
                    shape = CircleShape,
                )
                .clickable(onClick = onBackClicked),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.chat_back),
                modifier = Modifier.size(24.dp),
                tint = colorScheme.onBackground,
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .height(ChatHeaderHeight)
                .shadow(
                    elevation = FloatingSurfaceElevation,
                    shape = headerShape,
                    clip = false,
                )
                .clip(headerShape)
                .background(headerColor)
                .border(
                    width = 1.dp,
                    color = colorScheme.outline.copy(alpha = 0.72f),
                    shape = headerShape,
                )
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(avatarBrush(isDark)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.chat_avatar_text),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFFAFC),
                )
            }
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onBackground,
                maxLines = 1,
            )
        }
    }
}

private val ContentHorizontalPadding = 12.dp
private val ChatHeaderHeight = 44.dp
private val FloatingSurfaceElevation = 4.dp

private val DarkHeaderColor = Color(0xFF242129)
private val LightHeaderColor = Color(0xFFFFFEFF)

private val TopSystemBarBlurMask = Brush.verticalGradient(
    0f to Color.Black,
    0.55f to Color.Black,
    1f to Color.Transparent
)
private val BottomSystemBarBlurMask = Brush.verticalGradient(
    0f to Color.Transparent,
    0.45f to Color.Black,
    1f to Color.Black
)

private fun avatarBrush(isDark: Boolean): Brush = Brush.linearGradient(
    colors = if (isDark) {
        listOf(Color(0xFFB181E8), Color(0xFF70409D))
    } else {
        listOf(Color(0xFF9C67DB), Color(0xFF6D32AD))
    },
)
