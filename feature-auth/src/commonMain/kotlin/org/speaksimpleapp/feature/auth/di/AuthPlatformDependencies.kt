package org.speaksimpleapp.feature.auth.di

import org.speaksimpleapp.feature.auth.data.identity.GoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.local.SecureSessionStorage

internal interface AuthPlatformDependencies {
    val googleIdentityProvider: GoogleIdentityProvider
    val secureSessionStorage: SecureSessionStorage
}
