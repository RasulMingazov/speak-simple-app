package org.speaksimpleapp.feature.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.speaksimpleapp.feature.chat.presentation.ui.ChatContent

@Composable
fun RootContent(component: RootComponent) {
    ChatContent(
        component = component.chatComponent,
        modifier = Modifier
    )
}
