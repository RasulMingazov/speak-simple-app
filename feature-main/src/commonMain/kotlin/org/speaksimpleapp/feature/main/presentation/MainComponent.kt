package org.speaksimpleapp.feature.main.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import org.speaksimpleapp.feature.chat.presentation.ChatComponent

interface MainComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Chat(val component: ChatComponent) : Child
    }

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): MainComponent
    }
}
