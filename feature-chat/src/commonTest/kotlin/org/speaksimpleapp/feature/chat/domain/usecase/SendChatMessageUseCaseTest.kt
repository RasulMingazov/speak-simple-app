package org.speaksimpleapp.feature.chat.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest
import org.speaksimpleapp.feature.chat.data.FakeChatRepository
import org.speaksimpleapp.feature.chat.domain.model.ChatId
import org.speaksimpleapp.feature.chat.domain.model.MessageInputType

class SendChatMessageUseCaseTest {

    @Test
    fun createsCommandDataForRepository() = runTest {
        val repository = FakeChatRepository()
        repository.getChat(forceUpdate = false)
        val useCase = SendChatMessageUseCase(repository)

        val result = useCase(
            chatId = ChatId("weekend-plans"),
            text = "Hello",
            inputType = MessageInputType.TEXT,
        )

        assertEquals(ChatId("weekend-plans"), result.userMessage.chatId)
        assertEquals("Hello", result.userMessage.text)
        assertEquals(MessageInputType.TEXT, result.userMessage.inputType)
        assertNotNull(result.userMessage.clientMessageId)
    }
}
