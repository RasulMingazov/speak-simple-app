package org.speaksimpleapp.feature.chat.presentation

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
import org.speaksimpleapp.feature.chat.domain.usecase.LoadChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.ObserveChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.SendChatMessageUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @Test
    fun loadsInitialMessages() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isInitialLoading)
        assertTrue(state.messages.isNotEmpty())
        assertTrue(state.hasMorePrevious)
    }

    @Test
    fun sendsMessageThroughObservedMessages() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.dispatch(ChatComponent.Event.MessageChanged("Hello"))
        viewModel.dispatch(ChatComponent.Event.SendClicked)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSending)
        assertEquals("Hello", state.messages[state.messages.lastIndex - 1].text)
        assertTrue(state.messages.last().text.contains("Got it"))
    }

    @Test
    fun sendsScrollNewsAfterInitialMessagesLoaded() = runTest {
        val viewModel = createViewModel()
        val news = async { viewModel.news.first() }

        advanceUntilIdle()

        assertEquals(
            expected = ChatComponent.News.ScrollToBottom,
            actual = news.await()
        )
    }

    @Test
    fun sendsScrollNewsAfterMessageSent() = runTest {
        val viewModel = createViewModel()
        val initialNews = async { viewModel.news.first() }
        advanceUntilIdle()
        initialNews.await()

        val sendNews = async { viewModel.news.first() }
        viewModel.dispatch(ChatComponent.Event.MessageChanged("Hello"))
        viewModel.dispatch(ChatComponent.Event.SendClicked)
        advanceUntilIdle()

        assertEquals(
            expected = ChatComponent.News.ScrollToBottom,
            actual = sendNews.await()
        )
    }

    private fun TestScope.createViewModel(): ChatViewModel {
        val repository = FakeChatRepository()

        return ChatViewModel(
            loadChatMessagesUseCase = LoadChatMessagesUseCase(repository),
            observeChatMessagesUseCase = ObserveChatMessagesUseCase(repository),
            sendChatMessageUseCase = SendChatMessageUseCase(repository),
            coroutineDispatchers = object : CoroutineDispatchers {
                override val main: CoroutineDispatcher = StandardTestDispatcher(testScheduler)
            }
        )
    }
}
