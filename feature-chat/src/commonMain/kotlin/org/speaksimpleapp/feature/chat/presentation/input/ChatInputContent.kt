package org.speaksimpleapp.feature.chat.presentation.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import speak_simple_app.feature_chat.generated.resources.Res
import speak_simple_app.feature_chat.generated.resources.chat_message_placeholder
import speak_simple_app.feature_chat.generated.resources.chat_limit_reached_message
import speak_simple_app.feature_chat.generated.resources.chat_limit_reached_title
import speak_simple_app.feature_chat.generated.resources.chat_send
import speak_simple_app.feature_chat.generated.resources.chat_sending
import speak_simple_app.feature_chat.generated.resources.chat_voice_message

@Composable
internal fun ChatInputContent(
    component: ChatInputComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.uiState.collectAsState()
    val onTextChanged = remember(component) {
        { message: String ->
            component.dispatch(ChatInputComponent.Event.MessageChanged(message))
        }
    }
    val onSendClicked = remember(component) {
        {
            component.dispatch(ChatInputComponent.Event.SendClicked)
        }
    }

    ChatInputContent(
        text = state.message,
        canSend = state.canSend,
        isSending = state.isSending,
        isLimitReached = state.isLimitReached,
        onTextChanged = onTextChanged,
        onSendClicked = onSendClicked,
        modifier = modifier,
    )
}

@Composable
private fun ChatInputContent(
    text: String,
    canSend: Boolean,
    isSending: Boolean,
    isLimitReached: Boolean,
    onTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLimitReached) {
        LimitReachedCard(modifier = modifier)
        return
    }

    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background.luminance() < 0.5f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = ChatInputElevation,
                shape = ChatInputContainerShape,
                clip = false
            )
            .clip(ChatInputContainerShape)
            .background(colorScheme.surface)
            .border(
                width = 1.dp,
                color = colorScheme.outline.copy(alpha = if (isDark) 0.72f else 0.82f),
                shape = ChatInputContainerShape
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MessageTextField(
            text = text,
            isEnabled = !isSending,
            onTextChanged = onTextChanged,
            onSendClicked = onSendClicked,
            canSend = canSend,
            colorScheme = colorScheme,
            isDark = isDark,
            modifier = Modifier.weight(1f)
        )
        VoiceAction(isDark = isDark)
        SendAction(
            enabled = canSend,
            isSending = isSending,
            isDark = isDark,
            onClick = onSendClicked
        )
    }
}

@Composable
private fun LimitReachedCard(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background.luminance() < 0.5f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = ChatInputElevation,
                shape = ChatInputContainerShape,
                clip = false,
            )
            .clip(ChatInputContainerShape)
            .background(colorScheme.surface)
            .border(
                width = 1.dp,
                color = colorScheme.outline.copy(alpha = if (isDark) 0.72f else 0.82f),
                shape = ChatInputContainerShape,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.chat_limit_reached_title),
            style = MaterialTheme.typography.titleSmall,
            color = colorScheme.onSurface,
        )
        Text(
            text = stringResource(Res.string.chat_limit_reached_message),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MessageTextField(
    text: String,
    isEnabled: Boolean,
    canSend: Boolean,
    colorScheme: ColorScheme,
    onTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(ChatInputTextFieldShape)
            .background(colorScheme.primary.copy(alpha = if (isDark) 0.17f else 0.08f))
            .border(
                width = if (isDark) 0.dp else 1.dp,
                color = colorScheme.primary.copy(alpha = 0.16f),
                shape = ChatInputTextFieldShape
            )
            .heightIn(min = ChatInputActionSize)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier.fillMaxWidth(),
            enabled = isEnabled,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface),
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
                TextFieldDecoration(
                    isEmpty = text.isEmpty(),
                    color = colorScheme.onSurfaceVariant,
                    content = innerTextField
                )
            }
        )
    }
}

@Composable
private fun TextFieldDecoration(
    isEmpty: Boolean,
    color: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.padding(vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (isEmpty) {
            Text(
                text = stringResource(Res.string.chat_message_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                color = color.copy(alpha = 0.62f)
            )
        }
        content()
    }
}

@Composable
private fun VoiceAction(isDark: Boolean) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(ChatInputActionSize)
            .clip(CircleShape)
            .background(colorScheme.surface)
            .border(
                width = 1.dp,
                color = colorScheme.outline.copy(alpha = if (isDark) 0.48f else 0.62f),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = stringResource(Res.string.chat_voice_message),
            modifier = Modifier.size(18.dp),
            tint = colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SendAction(
    enabled: Boolean,
    isSending: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(ChatInputActionSize)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = if (isDark) {
                        listOf(Color(0xFFB181E8), Color(0xFF70409D))
                    } else {
                        listOf(Color(0xFF9C67DB), Color(0xFF6D32AD))
                    }
                )
            )
            .graphicsLayer(alpha = if (enabled || isSending) 1f else 0.72f)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSending) {
            Text(
                text = stringResource(Res.string.chat_sending),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFFAFC)
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(Res.string.chat_send),
                modifier = Modifier.size(18.dp),
                tint = Color(0xFFFFFAFC)
            )
        }
    }
}

private val ChatInputContainerShape = RoundedCornerShape(28.dp)
private val ChatInputTextFieldShape = RoundedCornerShape(20.dp)
private val ChatInputElevation = 4.dp
private val ChatInputActionSize = 42.dp
