package org.speaksimpleapp.feature.main.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import org.speaksimpleapp.feature.chat.presentation.ChatComponent

internal class DefaultMainComponent(
    componentContext: ComponentContext,
    private val chatComponentFactory: ChatComponent.Factory,
) : MainComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()

    override val stack: Value<ChildStack<*, MainComponent.Child>> = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.Chat,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    private fun createChild(
        configuration: Configuration,
        componentContext: ComponentContext,
    ): MainComponent.Child = when (configuration) {
        Configuration.Chat -> MainComponent.Child.Chat(
            component = chatComponentFactory(componentContext),
        )
    }

    @Serializable
    private sealed interface Configuration {
        @Serializable
        data object Chat : Configuration
    }

    class Factory(
        private val chatComponentFactoryProvider: () -> ChatComponent.Factory,
    ) : MainComponent.Factory {
        override fun invoke(componentContext: ComponentContext): MainComponent =
            DefaultMainComponent(
                componentContext = componentContext,
                chatComponentFactory = chatComponentFactoryProvider(),
            )
    }
}
