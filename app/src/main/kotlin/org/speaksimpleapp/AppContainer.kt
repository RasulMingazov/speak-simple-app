package org.speaksimpleapp

import android.app.Application
import org.speaksimpleapp.feature.auth.data.platform.AndroidActivityProvider
import org.speaksimpleapp.feature.auth.di.createAndroidAuthContainer
import org.speaksimpleapp.feature.root.RootComponent
import org.speaksimpleapp.feature.root.di.DefaultRootContainer

internal class AppContainer(application: Application) {
    val activityProvider = AndroidActivityProvider()

    private val authContainer = createAndroidAuthContainer(
        applicationContext = application,
        activityProvider = activityProvider,
        apiBaseUrl = BuildConfig.API_BASE_URL,
        googleServerClientId = BuildConfig.GOOGLE_SERVER_CLIENT_ID,
    )

    val rootComponentFactory: RootComponent.Factory =
        DefaultRootContainer(authContainer).rootComponentFactory
}
