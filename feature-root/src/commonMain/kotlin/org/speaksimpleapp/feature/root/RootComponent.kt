package org.speaksimpleapp.feature.root

import com.arkivanov.decompose.ComponentContext
import org.speaksimpleapp.feature.chat.presentation.ChatComponent

interface RootComponent {
    val model: RootModel
    val chatComponent: ChatComponent

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): RootComponent
    }
}
