package org.speaksimpleapp.feature.auth.di

import android.content.Context
import org.speaksimpleapp.feature.auth.data.identity.AndroidGoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.identity.GoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.local.AndroidSecureSessionStorage
import org.speaksimpleapp.feature.auth.data.local.SecureSessionStorage
import org.speaksimpleapp.feature.auth.data.platform.AndroidActivityProvider
import org.speaksimpleapp.feature.auth.data.platform.entity.DevicePlatform

fun createAndroidAuthContainer(
    applicationContext: Context,
    apiBaseUrl: String,
    googleServerClientId: String,
): AndroidAuthContainer {
    val activityProvider = AndroidActivityProvider()
    val authContainer = DefaultAuthContainer(
        config = AuthRuntimeConfig(
            apiBaseUrl = apiBaseUrl,
            devicePlatform = DevicePlatform.ANDROID,
        ),
        platform = object : AuthPlatformDependencies {
            override val googleIdentityProvider: GoogleIdentityProvider =
                AndroidGoogleIdentityProvider(
                    activityProvider = activityProvider,
                    serverClientId = googleServerClientId,
                )
            override val secureSessionStorage: SecureSessionStorage =
                AndroidSecureSessionStorage(applicationContext)
        },
    )

    return DefaultAndroidAuthContainer(
        delegate = authContainer,
        activityProvider = activityProvider,
    )
}
