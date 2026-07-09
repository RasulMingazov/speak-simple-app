package org.speaksimpleapp.feature.chat.presentation.messages

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.core.common.presentation.BaseModel
import org.speaksimpleapp.feature.chat.domain.model.ChatMessages
import org.speaksimpleapp.feature.chat.domain.usecase.LoadChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.ObserveChatMessagesUseCase
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent.Event
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

    override fun dispatch(event: Event) = model.dispatch(event)

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
    private val loadChatMessagesUseCase: LoadChatMessagesUseCase,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    stateMapper: ChatMessagesStateMapper = DefaultChatMessagesStateMapper,
    coroutineDispatchers: CoroutineDispatchers
) : BaseModel(coroutineDispatchers) {

    private val dataState: MutableStateFlow<DataState> = MutableStateFlow(DataState())
    val uiState: StateFlow<UiState> = dataState.mapState(stateMapper::invoke)

    private val _news = Channel<News>(Channel.BUFFERED)
    val news = _news.receiveAsFlow()

    init {
        observeMessages()
        loadInitialMessages()
    }

    fun dispatch(event: Event) {
        when (event) {
            is Event.LoadPreviousMessages -> loadPreviousMessages(event.beforeMessageId)
        }
    }

    private fun observeMessages() {
        modelScope.launch {
            observeChatMessagesUseCase()
                .filterNotNull()
                .collect(::onMessagesChanged)
        }
    }

    private fun loadInitialMessages() {
        modelScope.launch {
            loadChatMessagesUseCase(forceUpdate = true)
        }
    }

    private fun loadPreviousMessages(beforeMessageId: String) {
        modelScope.launch {
            if (!dataState.value.canLoadPrevious()) return@launch

            dataState.update {
                it.copy(isPreviousLoading = true)
            }
            loadChatMessagesUseCase(
                beforeMessageId = beforeMessageId,
                forceUpdate = true
            )
            dataState.update {
                it.copy(isPreviousLoading = false)
            }
        }
    }

    private fun onMessagesChanged(messages: ChatMessages) {
        val currentState = dataState.value
        val shouldScrollToBottom = currentState.shouldScrollToBottom(messages)

        dataState.update {
            it.copy(
                messages = messages,
                isInitialLoading = false
            )
        }

        if (shouldScrollToBottom) {
            _news.trySend(News.ScrollToBottom)
        }
    }

    override fun onCleared() {
        _news.close()
    }

    private fun DataState.canLoadPrevious(): Boolean = !isInitialLoading && !isPreviousLoading

    private fun DataState.shouldScrollToBottom(newMessages: ChatMessages): Boolean {
        if (isPreviousLoading) return false

        val currentMessages = messages ?: return true
        return currentMessages.lastMessageId() != newMessages.lastMessageId()
    }

    private fun ChatMessages.lastMessageId(): String? = messages.lastOrNull()?.id

    internal data class DataState(
        val messages: ChatMessages? = null,
        val isInitialLoading: Boolean = true,
        val isPreviousLoading: Boolean = false
    )

    class Factory(
        private val loadChatMessagesUseCase: LoadChatMessagesUseCase,
        private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
        private val coroutineDispatchers: CoroutineDispatchers
    ) {
        operator fun invoke(): ChatMessagesModel = ChatMessagesModel(
            loadChatMessagesUseCase = loadChatMessagesUseCase,
            observeChatMessagesUseCase = observeChatMessagesUseCase,
            coroutineDispatchers = coroutineDispatchers
        )
    }
}
