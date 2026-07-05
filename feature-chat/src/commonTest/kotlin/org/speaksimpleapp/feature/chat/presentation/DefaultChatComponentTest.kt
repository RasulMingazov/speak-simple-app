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
import org.speaksimpleapp.feature.chat.data.FakeChatRepository
import org.speaksimpleapp.feature.chat.domain.usecase.LoadChatMessagesUseCase
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
        assertTrue(state.hasMoreOlder)
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

    private fun TestScope.createComponent(): ChatComponent =
        createFactory().invoke(DefaultComponentContext(lifecycle = LifecycleRegistry()))

    private fun TestScope.createFactory(): ChatComponent.Factory {
        val repository = FakeChatRepository()

        return DefaultChatComponent.Factory(
            loadChatMessagesUseCase = LoadChatMessagesUseCase(repository),
            sendChatMessageUseCase = SendChatMessageUseCase(repository),
            chatDispatchers = object : ChatDispatchers {
                override val main: CoroutineDispatcher = StandardTestDispatcher(testScheduler)
            }
        )
    }
}
