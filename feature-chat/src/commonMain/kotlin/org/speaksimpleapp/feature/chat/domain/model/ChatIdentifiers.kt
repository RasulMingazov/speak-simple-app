package org.speaksimpleapp.feature.chat.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class ChatId(val value: String)

@JvmInline
value class MessageId(val value: String)

@JvmInline
value class ClientMessageId(val value: String)
