package org.speaksimpleapp.feature.chat.presentation.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import speak_simple_app.feature_chat.generated.resources.Res
import speak_simple_app.feature_chat.generated.resources.chat_message_placeholder
import speak_simple_app.feature_chat.generated.resources.chat_send
import speak_simple_app.feature_chat.generated.resources.chat_sending

@Composable
internal fun ChatInputContent(
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
