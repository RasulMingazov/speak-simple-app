package org.speaksimpleapp.feature.chat.presentation.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.feature.chat.data.FakeChatRepository
import org.speaksimpleapp.feature.chat.domain.model.ChatId
import org.speaksimpleapp.feature.chat.domain.model.MessageSendingAvailability
import org.speaksimpleapp.feature.chat.domain.usecase.GetChatUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.ObserveChatUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.SendChatMessageUseCase
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesModel

@OptIn(ExperimentalCoroutinesApi::class)
class ChatInputModelTest {

    @Test
    fun sendsMessageAndReceivesContextualReply() = runTest {
        val fixture = createFixture()
        advanceUntilIdle()

        fixture.inputModel.dispatch(ChatInputComponent.Event.MessageChanged("Hello"))
        fixture.inputModel.dispatch(
            ChatInputComponent.Event.ChatChanged(
                chatId = ChatId("weekend-plans"),
                sendingAvailability = MessageSendingAvailability.Available(10),
            )
        )
        fixture.inputModel.dispatch(ChatInputComponent.Event.SendClicked)
        advanceUntilIdle()

        val messages = fixture.messagesModel.uiState.value.messageItems
        assertFalse(fixture.inputModel.uiState.value.isSending)
        assertEquals("Hello", messages[messages.lastIndex - 1].text)
        assertTrue(messages[messages.lastIndex - 1].suggestionCount > 0)
        assertEquals(ChatMessagesComponent.MessageType.Assistant, messages.last().type)
        assertTrue(messages.last().text.contains("tell me"))
    }

    private fun TestScope.createFixture(): Fixture {
        val repository = FakeChatRepository()
        val dispatchers = testDispatchers()
        val observeChatUseCase = ObserveChatUseCase(repository)

        return Fixture(
            messagesModel = ChatMessagesModel(
                getChatUseCase = GetChatUseCase(repository),
                observeChatUseCase = observeChatUseCase,
                coroutineDispatchers = dispatchers,
            ),
            inputModel = ChatInputModel(
                sendChatMessageUseCase = SendChatMessageUseCase(repository),
                coroutineDispatchers = dispatchers,
            ),
        )
    }

    private fun TestScope.testDispatchers(): CoroutineDispatchers =
        object : CoroutineDispatchers {
            override val main: CoroutineDispatcher = StandardTestDispatcher(testScheduler)
        }

    private data class Fixture(
        val messagesModel: ChatMessagesModel,
        val inputModel: ChatInputModel,
    )
}
