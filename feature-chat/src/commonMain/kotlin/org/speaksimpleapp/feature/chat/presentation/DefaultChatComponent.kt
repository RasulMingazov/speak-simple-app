package org.speaksimpleapp.feature.chat.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessages
import org.speaksimpleapp.feature.chat.presentation.ChatComponent.UiNews
import org.speaksimpleapp.feature.chat.domain.usecase.LoadChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.ObserveChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.SendChatMessageUseCase
import org.speaksimpleapp.feature.chat.presentation.ChatComponent.UiEvent

internal class DefaultChatComponent(
    componentContext: ComponentContext,
    loadChatMessagesUseCase: LoadChatMessagesUseCase,
    observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    sendChatMessageUseCase: SendChatMessageUseCase,
    coroutineDispatchers: CoroutineDispatchers
) : ChatComponent, ComponentContext by componentContext {
    private val store: ChatStore = instanceKeeper.getOrCreate {
        ChatStore(
            loadChatMessagesUseCase = loadChatMessagesUseCase,
            observeChatMessagesUseCase = observeChatMessagesUseCase,
            sendChatMessageUseCase = sendChatMessageUseCase,
            coroutineDispatchers = coroutineDispatchers
        )
    }

    override val uiState: Value<ChatComponent.UiState> = store.uiState
    override val news = store.news

    override fun handle(uiEvent: UiEvent) {
        when (uiEvent) {
            is UiEvent.MessageChanged -> store.onMessageChanged(uiEvent.message)
            is UiEvent.SendClicked -> store.onSendClicked()
            is UiEvent.LoadPreviousMessages -> store.onLoadPreviousMessages()
        }
    }

    private class ChatStore(
        private val loadChatMessagesUseCase: LoadChatMessagesUseCase,
        private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
        private val sendChatMessageUseCase: SendChatMessageUseCase,
        uiStateMapper: ChatUiStateMapper = DefaultChatUiStateMapper,
        coroutineDispatchers: CoroutineDispatchers
    ) : InstanceKeeper.Instance {

        private val scope = CoroutineScope(coroutineDispatchers.main + SupervisorJob())
        private val dataState: MutableValue<DataState> = MutableValue(DataState())
        val uiState: Value<ChatComponent.UiState> = dataState.map(uiStateMapper::invoke)
        private val _news = Channel<UiNews>(Channel.BUFFERED)
        val news = _news.receiveAsFlow()

        init {
            observeMessages()
            loadInitialMessages()
        }

        private fun observeMessages() {
            scope.launch {
                observeChatMessagesUseCase()
                    .filterNotNull()
                    .collect { messages ->
                        dataState.update {
                            it.copy(
                                messages = messages,
                                isInitialLoading = false
                            )
                        }
                        if ( dataState.value.messages == null) {
                            _news.trySend(UiNews.ScrollToBottom)
                        }
                    }
            }
        }

        private fun loadInitialMessages() {
            scope.launch {
                loadChatMessagesUseCase(
                    forceUpdate = true
                )
            }
        }

        fun onMessageChanged(message: String) {
            dataState.update {
                it.copy(inputMessage = message)
            }
        }

        fun onSendClicked() {
            val current = dataState.value
            val text = current.inputMessage.trim()
            if (text.isEmpty() || current.isSending) return

            dataState.update {
                it.copy(
                    inputMessage = "",
                    feedback = null,
                    isSending = true
                )
            }

            scope.launch {
                val response = sendChatMessageUseCase(
                    text = text,
                    context = dataState.value.messages?.messages.orEmpty()
                )

                dataState.update {
                    it.copy(
                        feedback = response.feedback,
                        isSending = false
                    )
                }
                _news.trySend(UiNews.ScrollToBottom)
            }
        }

        fun onLoadPreviousMessages() {
            val current = dataState.value
            val messages = current.messages ?: return
            val oldestMessageId = messages.oldestMessageId
            if (
                current.isInitialLoading ||
                current.isPreviousLoading ||
                !messages.hasMorePrevious ||
                oldestMessageId == null
            ) {
                return
            }

            dataState.update {
                it.copy(isPreviousLoading = true)
            }

            scope.launch {
                loadChatMessagesUseCase(
                    beforeMessageId = oldestMessageId,
                    forceUpdate = true
                )
                dataState.update {
                    it.copy(isPreviousLoading = false)
                }
            }
        }

        override fun onDestroy() {
            _news.close()
            scope.cancel()
        }

    }

    internal data class DataState(
        val inputMessage: String = "",
        val messages: ChatMessages? = null,
        val feedback: ChatFeedback? = null,
        val isInitialLoading: Boolean = true,
        val isPreviousLoading: Boolean = false,
        val isSending: Boolean = false
    )

    class Factory(
        private val loadChatMessagesUseCase: LoadChatMessagesUseCase,
        private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
        private val sendChatMessageUseCase: SendChatMessageUseCase,
        private val coroutineDispatchers: CoroutineDispatchers
    ) : ChatComponent.Factory {
        override fun invoke(componentContext: ComponentContext): ChatComponent =
            DefaultChatComponent(
                componentContext = componentContext,
                loadChatMessagesUseCase = loadChatMessagesUseCase,
                observeChatMessagesUseCase = observeChatMessagesUseCase,
                sendChatMessageUseCase = sendChatMessageUseCase,
                coroutineDispatchers = coroutineDispatchers
            )
    }

}
