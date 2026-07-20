package org.speaksimpleapp.feature.chat.presentation.messages

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.core.common.presentation.BaseModel
import org.speaksimpleapp.feature.chat.domain.model.ChatSnapshot
import org.speaksimpleapp.feature.chat.domain.usecase.GetChatUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.ObserveChatUseCase
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent.UiState
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent.News

internal class DefaultChatMessagesComponent(
    componentContext: ComponentContext,
    modelFactory: ChatMessagesModel.Factory
) : ChatMessagesComponent,
    ComponentContext by componentContext {

    private val model: ChatMessagesModel = instanceKeeper.getOrCreate(
        key = "ChatMessagesModel"
    ) { modelFactory() }

    override val uiState: StateFlow<UiState> = model.uiState
    override val news: Flow<News> = model.news

    class Factory(
        private val modelFactory: ChatMessagesModel.Factory
    ) : ChatMessagesComponent.Factory {

        override fun invoke(componentContext: ComponentContext): ChatMessagesComponent =
            DefaultChatMessagesComponent(
                componentContext = componentContext,
                modelFactory = modelFactory
            )
    }
}

internal class ChatMessagesModel(
    private val getChatUseCase: GetChatUseCase,
    private val observeChatUseCase: ObserveChatUseCase,
    uiStateMapper: ChatMessagesUiStateMapper = DefaultChatMessagesUiStateMapper,
    coroutineDispatchers: CoroutineDispatchers
) : BaseModel(coroutineDispatchers) {

    private val dataState: MutableStateFlow<DataState> = MutableStateFlow(DataState())
    val uiState: StateFlow<UiState> = dataState.mapState(uiStateMapper::invoke)

    private val _news = Channel<News>(Channel.BUFFERED)
    val news = _news.receiveAsFlow()

    init {
        observeChat()
        getChat()
    }

    private fun observeChat() {
        modelScope.launch {
            observeChatUseCase()
                .distinctUntilChanged()
                .filterNotNull()
                .collect(::onChatChanged)
        }
    }

    private fun getChat() {
        modelScope.launch {
            getChatUseCase(forceUpdate = true)
        }
    }

    private fun onChatChanged(snapshot: ChatSnapshot) {
        val currentState = dataState.value
        val shouldScrollToBottom = currentState.shouldScrollToBottom(snapshot)

        dataState.update {
            it.copy(snapshot = snapshot)
        }

        if (shouldScrollToBottom) {
            _news.trySend(News.ScrollToBottom)
        }
    }

    override fun onCleared() {
        _news.close()
    }

    private fun DataState.shouldScrollToBottom(newSnapshot: ChatSnapshot): Boolean {
        val currentSnapshot = snapshot ?: return true
        return currentSnapshot.lastMessageId() != newSnapshot.lastMessageId()
    }

    private fun ChatSnapshot.lastMessageId() = messages.lastOrNull()?.id

    internal data class DataState(
        val snapshot: ChatSnapshot? = null,
    )

    class Factory(
        private val getChatUseCase: GetChatUseCase,
        private val observeChatUseCase: ObserveChatUseCase,
        private val coroutineDispatchers: CoroutineDispatchers,
        private val uiStateMapper: ChatMessagesUiStateMapper = DefaultChatMessagesUiStateMapper,
    ) {
        operator fun invoke(): ChatMessagesModel = ChatMessagesModel(
            getChatUseCase = getChatUseCase,
            observeChatUseCase = observeChatUseCase,
            coroutineDispatchers = coroutineDispatchers,
            uiStateMapper = uiStateMapper,
        )
    }
}
