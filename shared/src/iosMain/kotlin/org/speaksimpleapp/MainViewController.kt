package org.speaksimpleapp

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.speaksimpleapp.feature.root.di.DefaultRootContainer
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val rootComponent = DefaultRootContainer().rootComponentFactory(
        componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry())
    )

    return ComposeUIViewController {
        App(rootComponent = rootComponent)
    }
}
