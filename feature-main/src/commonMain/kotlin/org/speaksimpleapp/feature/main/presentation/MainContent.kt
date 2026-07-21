package org.speaksimpleapp.feature.main.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import org.speaksimpleapp.feature.chat.presentation.ChatContent

@Composable
fun MainContent(
    component: MainComponent,
    modifier: Modifier = Modifier,
) {
    Children(
        stack = component.stack,
        modifier = modifier.fillMaxSize(),
    ) { child ->
        when (val instance = child.instance) {
            is MainComponent.Child.Chat -> ChatContent(instance.component)
        }
    }
}
