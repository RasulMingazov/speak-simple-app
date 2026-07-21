package org.speaksimpleapp.feature.auth.data.identity

internal interface GoogleIdentityProvider {
    suspend fun signIn(nonce: String): GoogleIdToken?
    suspend fun signOut()
}
