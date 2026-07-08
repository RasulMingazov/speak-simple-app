package org.speaksimpleapp.feature.chat.presentation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.core.common.presentation.BaseViewModel
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessages
import org.speaksimpleapp.feature.chat.domain.usecase.LoadChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.ObserveChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.SendChatMessageUseCase
import org.speaksimpleapp.feature.chat.presentation.ChatComponent.Event
import org.speaksimpleapp.feature.chat.presentation.ChatComponent.News

internal class ChatViewModel(
    private val loadChatMessagesUseCase: LoadChatMessagesUseCase,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    uiStateMapper: ChatUiStateMapper = DefaultChatUiStateMapper,
    coroutineDispatchers: CoroutineDispatchers
) : BaseViewModel(coroutineDispatchers) {

    private val dataState: MutableStateFlow<DataState> = MutableStateFlow(DataState())
    val uiState: StateFlow<ChatComponent.UiState> = dataState.mapState(uiStateMapper::invoke)

    private val _news = Channel<News>(Channel.BUFFERED)
    val news = _news.receiveAsFlow()

    init {
        observeMessages()
        loadInitialMessages()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            observeChatMessagesUseCase()
                .filterNotNull()
                .collect { messages ->
                    val shouldScrollToBottom = dataState.value.shouldScrollToBottom(messages)

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
        }
    }

    private fun loadInitialMessages() {
        viewModelScope.launch {
            loadChatMessagesUseCase(forceUpdate = true)
        }
    }

    fun dispatch(uiEvent: Event) {
        when (uiEvent) {
            is Event.MessageChanged -> onMessageChanged(uiEvent.message)
            is Event.SendClicked -> onSendClicked()
            is Event.LoadPreviousMessages -> onLoadPreviousMessages()
        }
    }

    private fun onMessageChanged(message: String) {
        dataState.update {
            it.copy(inputMessage = message)
        }
    }

    private fun onSendClicked() {
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

        viewModelScope.launch {
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
        }
    }

    private fun onLoadPreviousMessages() {
        val beforeMessageId = dataState.value.previousPageKey ?: return

        dataState.update {
            it.copy(isPreviousLoading = true)
        }

        viewModelScope.launch {
            loadChatMessagesUseCase(
                beforeMessageId = beforeMessageId,
                forceUpdate = true
            )
            dataState.update {
                it.copy(isPreviousLoading = false)
            }
        }
    }

    override fun onCleared() {
        _news.close()
    }

    internal data class DataState(
        val inputMessage: String = "",
        val messages: ChatMessages? = null,
        val feedback: ChatFeedback? = null,
        val isInitialLoading: Boolean = true,
        val isPreviousLoading: Boolean = false,
        val isSending: Boolean = false
    ) {
        val previousPageKey: String?
            get() {
                val messages = messages ?: return null
                if (isInitialLoading || isPreviousLoading || !messages.hasMorePrevious) return null
                return messages.oldestMessageId
            }

        fun shouldScrollToBottom(newMessages: ChatMessages): Boolean {
            val currentMessages = messages ?: return true
            if (isPreviousLoading) return false

            return lastMessageId(currentMessages) != lastMessageId(newMessages)
        }

        private fun lastMessageId(messages: ChatMessages): String? =
            messages.messages.lastOrNull()?.id
    }

    class Factory(
        private val loadChatMessagesUseCase: LoadChatMessagesUseCase,
        private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
        private val sendChatMessageUseCase: SendChatMessageUseCase,
        private val coroutineDispatchers: CoroutineDispatchers
    ) {
        operator fun invoke(): ChatViewModel = ChatViewModel(
            loadChatMessagesUseCase = loadChatMessagesUseCase,
            observeChatMessagesUseCase = observeChatMessagesUseCase,
            sendChatMessageUseCase = sendChatMessageUseCase,
            coroutineDispatchers = coroutineDispatchers
        )
    }
}
