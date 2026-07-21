package org.speaksimpleapp.feature.chat.di

import org.speaksimpleapp.feature.chat.presentation.ChatComponent

fun createChatComponentFactory(): ChatComponent.Factory =
    DefaultChatContainer().chatComponentFactory
