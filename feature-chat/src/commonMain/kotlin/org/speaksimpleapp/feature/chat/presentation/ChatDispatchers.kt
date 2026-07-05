package org.speaksimpleapp.feature.chat.presentation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface ChatDispatchers {
    val main: CoroutineDispatcher
}

internal object DefaultChatDispatchers : ChatDispatchers {
    override val main: CoroutineDispatcher = Dispatchers.Main.immediate
}
