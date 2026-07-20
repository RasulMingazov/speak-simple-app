package org.speaksimpleapp.feature.chat.presentation.input

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.core.common.presentation.BaseModel
import org.speaksimpleapp.feature.chat.domain.model.ChatId
import org.speaksimpleapp.feature.chat.domain.model.MessageInputType
import org.speaksimpleapp.feature.chat.domain.model.MessageSendingAvailability
import org.speaksimpleapp.feature.chat.domain.usecase.SendChatMessageUseCase
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputComponent.Event
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputComponent.UiState

internal class DefaultChatInputComponent(
    componentContext: ComponentContext,
    modelFactory: ChatInputModel.Factory
) : ChatInputComponent,
    ComponentContext by componentContext {

    private val model: ChatInputModel = instanceKeeper.getOrCreate(
        key = "ChatInputModel"
    ) { modelFactory() }

    override val uiState: StateFlow<UiState> = model.uiState

    override fun dispatch(event: Event) = model.dispatch(event)

    class Factory(
        private val modelFactory: ChatInputModel.Factory
    ) : ChatInputComponent.Factory {

        override fun invoke(componentContext: ComponentContext): ChatInputComponent =
            DefaultChatInputComponent(
                componentContext = componentContext,
                modelFactory = modelFactory
            )
    }
}

internal class ChatInputModel(
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    uiStateMapper: ChatInputUiStateMapper = DefaultChatInputUiStateMapper,
    coroutineDispatchers: CoroutineDispatchers
) : BaseModel(coroutineDispatchers) {

    private val dataState = MutableStateFlow(DataState())
    val uiState: StateFlow<UiState> = dataState.mapState(uiStateMapper::invoke)

    fun dispatch(event: Event) {
        when (event) {
            is Event.MessageChanged -> onMessageChanged(event)
            is Event.ChatChanged -> onChatChanged(event)
            Event.SendClicked -> onSendClicked()
        }
    }

    private fun onMessageChanged(event: Event.MessageChanged) {
        dataState.update {
            it.copy(message = event.message)
        }
    }

    private fun onChatChanged(event: Event.ChatChanged) {
        dataState.update {
            it.copy(
                chatId = event.chatId,
                sendingAvailability = event.sendingAvailability,
            )
        }
    }

    private fun onSendClicked() {
        modelScope.launch {
            val current = dataState.value
            val text = current.message.trim()
            val chatId = current.chatId ?: return@launch
            if (
                text.isEmpty() ||
                current.isSending ||
                current.sendingAvailability == MessageSendingAvailability.LimitReached
            ) return@launch

            dataState.update {
                it.copy(
                    message = "",
                    isSending = true
                )
            }
            sendChatMessageUseCase(
                chatId = chatId,
                text = text,
                inputType = MessageInputType.TEXT,
            )

            dataState.update {
                it.copy(isSending = false)
            }
        }
    }

    data class DataState(
        val message: String = "",
        val isSending: Boolean = false,
        val chatId: ChatId? = null,
        val sendingAvailability: MessageSendingAvailability? = null,
    )

    class Factory(
        private val sendChatMessageUseCase: SendChatMessageUseCase,
        private val coroutineDispatchers: CoroutineDispatchers
    ) {
        operator fun invoke(): ChatInputModel = ChatInputModel(
            sendChatMessageUseCase = sendChatMessageUseCase,
            coroutineDispatchers = coroutineDispatchers
        )
    }
}
