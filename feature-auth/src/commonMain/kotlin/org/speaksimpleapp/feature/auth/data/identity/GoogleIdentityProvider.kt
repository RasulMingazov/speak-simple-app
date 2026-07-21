package org.speaksimpleapp.feature.auth.data.identity

interface GoogleIdentityProvider {
    suspend fun signIn(nonce: String): GoogleIdToken?
    suspend fun signOut()
}
