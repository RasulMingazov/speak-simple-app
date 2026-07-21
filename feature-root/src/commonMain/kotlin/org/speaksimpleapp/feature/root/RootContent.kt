package org.speaksimpleapp.feature.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import org.speaksimpleapp.feature.auth.presentation.LoginContent
import org.speaksimpleapp.feature.main.presentation.MainContent

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    Children(
        stack = component.stack,
        modifier = modifier.fillMaxSize(),
    ) { child ->
        when (val instance = child.instance) {
            RootComponent.Child.Bootstrap -> BootstrapContent()
            is RootComponent.Child.Login -> LoginContent(instance.component)
            is RootComponent.Child.Main -> MainContent(instance.component)
        }
    }
}

@Composable
private fun BootstrapContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    )
}
