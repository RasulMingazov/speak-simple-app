package org.speaksimpleapp

import android.app.Application
import org.speaksimpleapp.feature.auth.data.identity.AndroidGoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.identity.GoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.local.AndroidSecureSessionStorage
import org.speaksimpleapp.feature.auth.data.local.SecureSessionStorage
import org.speaksimpleapp.feature.auth.data.platform.AndroidActivityProvider
import org.speaksimpleapp.feature.auth.data.platform.entity.DevicePlatform
import org.speaksimpleapp.feature.auth.di.AuthPlatformDependencies
import org.speaksimpleapp.feature.auth.di.AuthRuntimeConfig
import org.speaksimpleapp.feature.auth.di.DefaultAuthContainer
import org.speaksimpleapp.feature.root.RootComponent
import org.speaksimpleapp.feature.root.di.DefaultRootContainer

internal class AppContainer(application: Application) {
    val activityProvider = AndroidActivityProvider()

    private val authContainer = DefaultAuthContainer(
        config = AuthRuntimeConfig(
            apiBaseUrl = BuildConfig.API_BASE_URL,
            devicePlatform = DevicePlatform.ANDROID,
        ),
        platform = object : AuthPlatformDependencies {
            override val googleIdentityProvider: GoogleIdentityProvider =
                AndroidGoogleIdentityProvider(
                    activityProvider = activityProvider,
                    serverClientId = BuildConfig.GOOGLE_SERVER_CLIENT_ID,
                )
            override val secureSessionStorage: SecureSessionStorage =
                AndroidSecureSessionStorage(application)
        },
    )

    val rootComponentFactory: RootComponent.Factory =
        DefaultRootContainer(authContainer).rootComponentFactory
}
