package org.speaksimpleapp

import android.app.Application

class SpeakSimpleApplication : Application() {
    internal val container: AppContainer by lazy {
        AppContainer(this)
    }
}
