package org.speaksimpleapp.feature.main.presentation

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.speaksimpleapp.feature.chat.presentation.ChatComponent
import org.speaksimpleapp.feature.chat.presentation.input.ChatInputComponent
import org.speaksimpleapp.feature.chat.presentation.messages.ChatMessagesComponent
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultMainComponentTest {
    @Test
    fun `creates user scoped chat factory for every main component`() {
        var chatFactoryCreationCount = 0
        val factory = DefaultMainComponent.Factory(
            chatComponentFactoryProvider = {
                chatFactoryCreationCount += 1
                ChatComponent.Factory { FakeChatComponent }
            },
        )

        factory(DefaultComponentContext(lifecycle = LifecycleRegistry()))
        factory(DefaultComponentContext(lifecycle = LifecycleRegistry()))

        assertEquals(2, chatFactoryCreationCount)
    }
}

private object FakeChatComponent : ChatComponent {
    override val messages: ChatMessagesComponent
        get() = error("Not used in this test")
    override val input: ChatInputComponent
        get() = error("Not used in this test")
}
