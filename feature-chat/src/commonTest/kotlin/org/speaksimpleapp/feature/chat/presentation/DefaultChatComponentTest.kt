package org.speaksimpleapp.feature.chat.presentation

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
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
import org.speaksimpleapp.feature.chat.domain.model.ChatMessage
import org.speaksimpleapp.feature.chat.domain.model.ChatMessages
import org.speaksimpleapp.feature.chat.domain.model.ChatRole
import org.speaksimpleapp.feature.chat.domain.usecase.LoadChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.ObserveChatMessagesUseCase
import org.speaksimpleapp.feature.chat.domain.usecase.SendChatMessageUseCase

class DefaultChatComponentTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadsInitialMessages() = runTest {
        val component = createComponent()

        advanceUntilIdle()

        val state = component.uiState.value
        assertFalse(state.isInitialLoading)
        assertTrue(state.messages.isNotEmpty())
        assertTrue(state.hasMorePrevious)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun keepsStateWhenComponentIsCreatedAgainWithSameContext() = runTest {
        val componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry())
        val factory = createFactory()
        val firstComponent = factory(componentContext)

        advanceUntilIdle()
        firstComponent.handle(ChatComponent.UiEvent.MessageChanged("Hello there"))

        val recreatedComponent = factory(componentContext)

        assertEquals(
            expected = "Hello there",
            actual = recreatedComponent.uiState.value.inputMessage
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun sendsMessageThroughObservedMessages() = runTest {
        val component = createComponent()
        advanceUntilIdle()

        component.handle(ChatComponent.UiEvent.MessageChanged("Hello"))
        component.handle(ChatComponent.UiEvent.SendClicked)
        advanceUntilIdle()

        val state = component.uiState.value
        assertFalse(state.isSending)
        assertEquals("Hello", state.messages[state.messages.lastIndex - 1].text)
        assertTrue(state.messages.last().text.contains("Got it"))
    }

    @Test
    fun mapperShowsInitialLoadingFromDataState() {
        val uiState = DefaultChatUiStateMapper(
            DefaultChatComponent.DataState(
                messages = ChatMessages(
                    messages = emptyList(),
                    oldestMessageId = null,
                    hasMorePrevious = false
                ),
                isInitialLoading = true
            )
        )

        assertTrue(uiState.isInitialLoading)
    }

    @Test
    fun mapperMapsMessagesAndPagination() {
        val uiState = DefaultChatUiStateMapper(
            DefaultChatComponent.DataState(
                messages = ChatMessages(
                    messages = listOf(
                        ChatMessage(
                            id = "1",
                            role = ChatRole.Assistant,
                            text = "Hi"
                        )
                    ),
                    oldestMessageId = "1",
                    hasMorePrevious = true
                ),
                isInitialLoading = false
            )
        )

        assertFalse(uiState.isInitialLoading)
        assertTrue(uiState.hasMorePrevious)
    }

    private fun TestScope.createComponent(): ChatComponent =
        createFactory().invoke(DefaultComponentContext(lifecycle = LifecycleRegistry()))

    private fun TestScope.createFactory(): ChatComponent.Factory {
        val repository = FakeChatRepository()

        return DefaultChatComponent.Factory(
            loadChatMessagesUseCase = LoadChatMessagesUseCase(repository),
            observeChatMessagesUseCase = ObserveChatMessagesUseCase(repository),
            sendChatMessageUseCase = SendChatMessageUseCase(repository),
            coroutineDispatchers = object : CoroutineDispatchers {
                override val main: CoroutineDispatcher = StandardTestDispatcher(testScheduler)
            }
        )
    }
}
