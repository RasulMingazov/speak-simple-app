package org.speaksimpleapp.feature.root.di

import org.speaksimpleapp.feature.root.RootComponent

interface RootContainer {
    val rootComponentFactory: RootComponent.Factory
}
