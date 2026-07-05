package org.speaksimpleapp

import androidx.compose.runtime.Composable
import org.speaksimpleapp.core.design.theme.SpeakSimpleTheme
import org.speaksimpleapp.feature.root.RootComponent
import org.speaksimpleapp.feature.root.RootContent

@Composable
fun App(rootComponent: RootComponent) {
    SpeakSimpleTheme {
        RootContent(component = rootComponent)
    }
}
