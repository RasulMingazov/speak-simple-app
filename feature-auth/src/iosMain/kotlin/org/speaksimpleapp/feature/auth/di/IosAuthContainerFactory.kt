package org.speaksimpleapp.feature.auth.di

import org.speaksimpleapp.feature.auth.data.identity.GoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.identity.IosGoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.local.IosSecureSessionStorage
import org.speaksimpleapp.feature.auth.data.local.SecureSessionStorage
import org.speaksimpleapp.feature.auth.data.platform.IosAuthPlatformBridge
import org.speaksimpleapp.feature.auth.data.platform.entity.DevicePlatform

fun createIosAuthContainer(
    apiBaseUrl: String,
    bridge: IosAuthPlatformBridge,
): AuthContainer = DefaultAuthContainer(
    config = AuthRuntimeConfig(
        apiBaseUrl = apiBaseUrl,
        devicePlatform = DevicePlatform.IOS,
    ),
    platform = object : AuthPlatformDependencies {
        override val googleIdentityProvider: GoogleIdentityProvider =
            IosGoogleIdentityProvider(bridge)
        override val secureSessionStorage: SecureSessionStorage =
            IosSecureSessionStorage(bridge)
    },
)
