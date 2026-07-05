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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.speaksimpleapp.feature.chat.presentation.ChatComponent.UiEvent
import org.speaksimpleapp.feature.chat.domain.model.ChatFeedback
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatRole
import org.speaksimpleapp.feature.chat.domain.usecase.LoadChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.SendChatMessageUseCase

internal class DefaultChatComponent(
    componentContext: ComponentContext,
    loadChatMessagesUseCase: LoadChatMessagesUseCase,
    sendChatMessageUseCase: SendChatMessageUseCase,
    chatDispatchers: ChatDispatchers
) : ChatComponent, ComponentContext by componentContext {
    private val store: ChatStore = instanceKeeper.getOrCreate {
        ChatStore(
            loadChatMessagesUseCase = loadChatMessagesUseCase,
            sendChatMessageUseCase = sendChatMessageUseCase,
            chatDispatchers = chatDispatchers
        )
    }

    override val uiState: Value<ChatComponent.UiState> = store.uiState

    override fun handle(uiEvent: UiEvent) {
        when (uiEvent) {
            is UiEvent.MessageChanged -> store.onMessageChanged(uiEvent.message)
            is UiEvent.SendClicked -> store.onSendClicked()
            is UiEvent.LoadOlderMessages -> store.loadOlderMessages()
        }
    }

    private class ChatStore(
        private val loadChatMessagesUseCase: LoadChatMessagesUseCase,
        private val sendChatMessageUseCase: SendChatMessageUseCase,
        chatDispatchers: ChatDispatchers
    ) : InstanceKeeper.Instance {

        private val scope = CoroutineScope(chatDispatchers.main + SupervisorJob())
        private val dataState: MutableValue<DataState> = MutableValue(DataState())
        val uiState: Value<ChatComponent.UiState> = dataState.map(DataState::toUi)

        init {
            loadInitialMessages()
        }

        override fun onDestroy() {
            scope.cancel()
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

            val userMessage = ChatMessage(
                id = "user-${current.messages.size}",
                role = ChatRole.User,
                text = text
            )
            val context = current.messages + userMessage

            dataState.update {
                it.copy(
                    inputMessage = "",
                    messages = context,
                    feedback = null,
                    isSending = true,
                    scrollToBottomRequest = it.scrollToBottomRequest + 1
                )
            }

            scope.launch {
                val response = sendChatMessageUseCase(
                    text = text,
                    context = context
                )

                dataState.update {
                    it.copy(
                        messages = it.messages + ChatMessage(
                            id = "assistant-${it.messages.size}",
                            role = ChatRole.Assistant,
                            text = response.answer
                        ),
                        feedback = response.feedback,
                        isSending = false,
                        scrollToBottomRequest = it.scrollToBottomRequest + 1
                    )
                }
            }
        }

        private fun loadInitialMessages() {
            scope.launch {
                val page = loadChatMessagesUseCase(
                    beforeMessageId = null,
                    limit = PAGE_SIZE
                )

                dataState.update {
                    it.copy(
                        messages = page.messages,
                        nextCursor = page.nextCursor,
                        hasMoreOlder = page.hasMore,
                        isInitialLoading = false,
                        scrollToBottomRequest = it.scrollToBottomRequest + 1
                    )
                }
            }
        }

        fun loadOlderMessages() {
            val current = dataState.value
            if (current.isInitialLoading || current.isLoadingOlder || !current.hasMoreOlder) return

            dataState.update {
                it.copy(isLoadingOlder = true)
            }

            scope.launch {
                val page = loadChatMessagesUseCase(
                    beforeMessageId = current.nextCursor,
                    limit = PAGE_SIZE
                )

                dataState.update {
                    it.copy(
                        messages = page.messages + it.messages,
                        nextCursor = page.nextCursor,
                        hasMoreOlder = page.hasMore,
                        isLoadingOlder = false
                    )
                }
            }
        }

        private data class DataState(
            val inputMessage: String = "",
            val messages: List<ChatMessage> = emptyList(),
            val nextCursor: String? = null,
            val hasMoreOlder: Boolean = false,
            val isInitialLoading: Boolean = true,
            val isLoadingOlder: Boolean = false,
            val feedback: ChatFeedback? = null,
            val isSending: Boolean = false,
            val scrollToBottomRequest: Int = 0
        ) {
            fun toUi(): ChatComponent.UiState =
                ChatComponent.UiState(
                    title = "SpeakSimple Chat",
                    subtitle = "Text practice now. Voice messages come next.",
                    inputMessage = inputMessage,
                    messages = messages,
                    feedback = feedback,
                    isInitialLoading = isInitialLoading,
                    isLoadingOlder = isLoadingOlder,
                    hasMoreOlder = hasMoreOlder,
                    isSending = isSending,
                    scrollToBottomRequest = scrollToBottomRequest
                )
        }
    }

    class Factory(
        private val loadChatMessagesUseCase: LoadChatMessagesUseCase,
        private val sendChatMessageUseCase: SendChatMessageUseCase,
        private val chatDispatchers: ChatDispatchers
    ) : ChatComponent.Factory {
        override fun invoke(componentContext: ComponentContext): ChatComponent =
            DefaultChatComponent(
                componentContext = componentContext,
                loadChatMessagesUseCase = loadChatMessagesUseCase,
                sendChatMessageUseCase = sendChatMessageUseCase,
                chatDispatchers = chatDispatchers
            )
    }
    private companion object {
        const val PAGE_SIZE = 12
    }

}
