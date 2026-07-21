package org.speaksimpleapp.feature.main.di

import org.speaksimpleapp.feature.chat.di.createChatComponentFactory
import org.speaksimpleapp.feature.main.presentation.DefaultMainComponent
import org.speaksimpleapp.feature.main.presentation.MainComponent

fun createMainComponentFactory(): MainComponent.Factory =
    DefaultMainComponent.Factory(
        chatComponentFactoryProvider = ::createChatComponentFactory,
    )
