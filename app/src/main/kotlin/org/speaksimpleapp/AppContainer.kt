package org.speaksimpleapp

import android.app.Application
import org.speaksimpleapp.feature.auth.di.createAndroidAuthContainer
import org.speaksimpleapp.feature.root.RootComponent
import org.speaksimpleapp.feature.root.di.createRootComponentFactory

internal class AppContainer(application: Application) {
    private val authContainer = createAndroidAuthContainer(
        applicationContext = application,
        apiBaseUrl = BuildConfig.API_BASE_URL,
        googleServerClientId = BuildConfig.GOOGLE_SERVER_CLIENT_ID,
    )

    val rootComponentFactory: RootComponent.Factory =
        createRootComponentFactory(authContainer)

    fun attachActivity(activity: MainActivity) = authContainer.attachActivity(activity)

    fun detachActivity(activity: MainActivity) = authContainer.detachActivity(activity)
}
