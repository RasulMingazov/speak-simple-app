package org.speaksimpleapp.feature.chat.presentation.messages

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

@OptIn(ExperimentalCoroutinesApi::class)
class ChatMessagesModelTest {

    @Test
    fun loadsInitialMessages() = runTest {
        val model = createModel()

        advanceUntilIdle()

        val state = model.uiState.value
        assertFalse(state.isInitialLoading)
        assertTrue(state.messages.isNotEmpty())
        assertTrue(state.hasMorePrevious)
    }

    @Test
    fun sendsScrollNewsAfterInitialMessagesLoaded() = runTest {
        val model = createModel()
        val news = async { model.news.first() }

        advanceUntilIdle()

        assertEquals(
            expected = ChatMessagesComponent.News.ScrollToBottom,
            actual = news.await()
        )
    }

    @Test
    fun loadsPreviousMessages() = runTest {
        val model = createModel()
        advanceUntilIdle()

        val initialState = model.uiState.value
        val initialFirstMessageId = initialState.messages.first().id
        val previousPageKey = requireNotNull(initialState.previousPageKey)

        model.dispatch(
            ChatMessagesComponent.Event.LoadPreviousMessages(
                beforeMessageId = previousPageKey
            )
        )
        advanceUntilIdle()

        val updatedState = model.uiState.value
        assertTrue(updatedState.messages.size > initialState.messages.size)
        assertFalse(updatedState.isPreviousLoading)
        assertTrue(updatedState.messages.any { it.id == initialFirstMessageId })
        assertFalse(updatedState.messages.first().id == initialFirstMessageId)
    }

    private fun TestScope.createModel(): ChatMessagesModel {
        val repository = FakeChatRepository()

        return ChatMessagesModel(
            loadChatMessagesUseCase = LoadChatMessagesUseCase(repository),
            observeChatMessagesUseCase = ObserveChatMessagesUseCase(repository),
            coroutineDispatchers = object : CoroutineDispatchers {
                override val main: CoroutineDispatcher = StandardTestDispatcher(testScheduler)
            }
        )
    }
}
