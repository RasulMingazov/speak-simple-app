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
import org.speaksimpleapp.feature.chat.domain.usecase.GetChatUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.ObserveChatUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class ChatMessagesModelTest {

    @Test
    fun loadsWholeChat() = runTest {
        val model = createModel()

        advanceUntilIdle()

        val state = model.uiState.value
        assertFalse(state.isInitialLoading)
        assertEquals("Weekend plans", state.title)
        assertTrue(state.messageItems.isNotEmpty())
    }

    @Test
    fun sendsScrollNewsAfterChatLoaded() = runTest {
        val model = createModel()
        val news = async { model.news.first() }

        advanceUntilIdle()

        assertEquals(ChatMessagesComponent.News.ScrollToBottom, news.await())
    }

    private fun TestScope.createModel(): ChatMessagesModel {
        val repository = FakeChatRepository()
        return ChatMessagesModel(
            getChatUseCase = GetChatUseCase(repository),
            observeChatUseCase = ObserveChatUseCase(repository),
            coroutineDispatchers = testDispatchers(),
        )
    }

    private fun TestScope.testDispatchers(): CoroutineDispatchers =
        object : CoroutineDispatchers {
            override val main: CoroutineDispatcher = StandardTestDispatcher(testScheduler)
        }
}
