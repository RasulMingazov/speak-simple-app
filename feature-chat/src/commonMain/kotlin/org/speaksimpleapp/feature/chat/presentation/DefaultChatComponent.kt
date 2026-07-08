package org.speaksimpleapp.feature.chat.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.StateFlow

internal class DefaultChatComponent(
    componentContext: ComponentContext,
    chatViewModelFactory: ChatViewModel.Factory
) : ChatComponent, ComponentContext by componentContext {

    private val viewModel: ChatViewModel = instanceKeeper.getOrCreate {
        chatViewModelFactory()
    }

    override val uiState: StateFlow<ChatComponent.UiState> = viewModel.uiState
    override val news = viewModel.news

    override fun dispatch(uiEvent: ChatComponent.Event) = viewModel.dispatch(uiEvent)

    class Factory(
        private val chatViewModelFactory: ChatViewModel.Factory,
    ) : ChatComponent.Factory {

        override fun invoke(componentContext: ComponentContext): ChatComponent =
            DefaultChatComponent(
                componentContext = componentContext,
                chatViewModelFactory = chatViewModelFactory
            )
    }
}
