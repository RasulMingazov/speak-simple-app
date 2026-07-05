package org.speaksimpleapp.feature.chat.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
internal fun chatPalette(): ChatPalette {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    return if (isDark) {
        ChatPalette(
            backgroundTop = Color(0xFF111116),
            backgroundMid = Color(0xFF17171E),
            backgroundBottom = Color(0xFF101014),
            primaryText = Color(0xFFF6F3FB),
            secondaryText = Color(0xFFB9B4C2),
            accent = Color(0xFFBFA2FF),
            onAccent = Color(0xFF201632),
            avatarBackground = Color(0xFF3B2A59),
            avatarText = Color(0xFFEBDDFF),
            assistantBubble = Color(0xFF25242C),
            assistantBubbleBorder = Color(0xFF34323D),
            assistantText = Color(0xFFE8E4EE),
            userBubble = Color(0xFFD8C6FF),
            userBubbleBorder = Color(0xFFB99EF4),
            userText = Color(0xFF261B3A),
            coachBackground = Color(0xFF37301D),
            coachBorder = Color(0xFF7F6B33),
            coachAccent = Color(0xFFFFD66B),
            coachText = Color(0xFFFFF2C8),
            composerBackground = Color(0xFF1D1C24),
            composerBorder = Color(0xFF34313D),
            composerActionBackground = Color(0xFF2C2934)
        )
    } else {
        ChatPalette(
            backgroundTop = Color(0xFFFCFBFF),
            backgroundMid = Color(0xFFF3EEFF),
            backgroundBottom = Color(0xFFFFF9EF),
            primaryText = Color(0xFF181521),
            secondaryText = Color(0xFF625B70),
            accent = Color(0xFF7652D6),
            onAccent = Color.White,
            avatarBackground = Color(0xFFE5D9FF),
            avatarText = Color(0xFF3B1688),
            assistantBubble = Color(0xFFF0EDF7),
            assistantBubbleBorder = Color(0xFFE5DEEF),
            assistantText = Color(0xFF4D4857),
            userBubble = Color(0xFFE4D8FF),
            userBubbleBorder = Color(0xFFCDBBFF),
            userText = Color(0xFF271445),
            coachBackground = Color(0xFFFFEDB7),
            coachBorder = Color(0xFFE2C86B),
            coachAccent = Color(0xFF967A00),
            coachText = Color(0xFF25202C),
            composerBackground = Color.White,
            composerBorder = Color(0xFFE0DAEA),
            composerActionBackground = Color(0xFFF0EDF6)
        )
    }
}

internal data class ChatPalette(
    val backgroundTop: Color,
    val backgroundMid: Color,
    val backgroundBottom: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val accent: Color,
    val onAccent: Color,
    val avatarBackground: Color,
    val avatarText: Color,
    val assistantBubble: Color,
    val assistantBubbleBorder: Color,
    val assistantText: Color,
    val userBubble: Color,
    val userBubbleBorder: Color,
    val userText: Color,
    val coachBackground: Color,
    val coachBorder: Color,
    val coachAccent: Color,
    val coachText: Color,
    val composerBackground: Color,
    val composerBorder: Color,
    val composerActionBackground: Color
)
