package org.speaksimpleapp.feature.chat.presentation.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.feature.chat.data.FakeChatRepository
import org.speaksimpleapp.feature.chat.domain.model.ChatRole
import org.speaksimpleapp.feature.chat.domain.repository.ChatRepository
import org.speaksimpleapp.feature.chat.domain.usecase.LoadChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.ObserveChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.SendChatMessageUseCase
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesModel

@OptIn(ExperimentalCoroutinesApi::class)
class ChatInputModelTest {

    @Test
    fun sendsMessageThroughObservedMessages() = runTest {
        val fixture = createFixture()
        advanceUntilIdle()

        fixture.inputModel.dispatch(ChatInputComponent.Event.MessageChanged("Hello"))
        fixture.inputModel.dispatch(ChatInputComponent.Event.SendClicked)
        advanceUntilIdle()

        val messagesState = fixture.messagesModel.uiState.value
        val inputState = fixture.inputModel.uiState.value
        assertFalse(inputState.isSending)
        assertEquals("Hello", messagesState.messages[messagesState.messages.lastIndex - 2].text)
        assertTrue(messagesState.messages[messagesState.messages.lastIndex - 1].text.contains("Got it"))
        assertEquals(ChatRole.Feedback, messagesState.messages.last().role)
        assertTrue(messagesState.messages.last().text.contains("More natural"))
    }

    @Test
    fun sendsScrollNewsAfterMessageSent() = runTest {
        val fixture = createFixture()
        val initialNews = async { fixture.messagesModel.news.first() }
        advanceUntilIdle()
        initialNews.await()

        val sendNews = async { fixture.messagesModel.news.first() }
        fixture.inputModel.dispatch(ChatInputComponent.Event.MessageChanged("Hello"))
        fixture.inputModel.dispatch(ChatInputComponent.Event.SendClicked)
        advanceUntilIdle()

        assertEquals(
            expected = ChatMessagesComponent.News.ScrollToBottom,
            actual = sendNews.await()
        )
    }

    private fun TestScope.createFixture(): Fixture {
        val repository = FakeChatRepository()
        val dispatchers = testDispatchers()
        val messagesModel = createMessagesModel(
            repository = repository,
            dispatchers = dispatchers
        )
        val inputModel = ChatInputModel(
            sendChatMessageUseCase = SendChatMessageUseCase(repository),
            coroutineDispatchers = dispatchers
        )

        return Fixture(
            messagesModel = messagesModel,
            inputModel = inputModel
        )
    }

    private fun TestScope.createMessagesModel(
        repository: ChatRepository,
        dispatchers: CoroutineDispatchers
    ): ChatMessagesModel =
        ChatMessagesModel(
            loadChatMessagesUseCase = LoadChatMessagesUseCase(repository),
            observeChatMessagesUseCase = ObserveChatMessagesUseCase(repository),
            coroutineDispatchers = dispatchers
        )

    private fun TestScope.testDispatchers(): CoroutineDispatchers =
        object : CoroutineDispatchers {
            override val main: CoroutineDispatcher = StandardTestDispatcher(testScheduler)
        }

    private data class Fixture(
        val messagesModel: ChatMessagesModel,
        val inputModel: ChatInputModel
    )
}
