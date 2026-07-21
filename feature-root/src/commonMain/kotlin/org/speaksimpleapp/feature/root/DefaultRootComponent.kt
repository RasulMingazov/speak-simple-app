package org.speaksimpleapp.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.feature.auth.presentation.LoginComponent
import org.speaksimpleapp.feature.chat.presentation.ChatComponent

internal class DefaultRootComponent(
    componentContext: ComponentContext,
    modelFactory: RootModel.Factory,
    loginComponentFactory: LoginComponent.Factory,
    chatComponentFactory: ChatComponent.Factory,
) : RootComponent, ComponentContext by componentContext {
    private val model = instanceKeeper.getOrCreate("RootModel", modelFactory::invoke)
    override val uiState: StateFlow<RootComponent.UiState> = model.uiState
    override val loginComponent = loginComponentFactory(childContext("LOGIN"))
    override val chatComponent = chatComponentFactory(childContext("CHAT"))

    class Factory(
        private val modelFactory: RootModel.Factory,
        private val loginComponentFactory: LoginComponent.Factory,
        private val chatComponentFactory: ChatComponent.Factory,
    ) : RootComponent.Factory {
        override fun invoke(componentContext: ComponentContext): RootComponent = DefaultRootComponent(
            componentContext,
            modelFactory,
            loginComponentFactory,
            chatComponentFactory,
        )
    }
}
