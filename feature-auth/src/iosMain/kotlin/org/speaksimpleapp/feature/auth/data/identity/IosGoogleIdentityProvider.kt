package org.speaksimpleapp.feature.auth.data.identity

import kotlinx.coroutines.suspendCancellableCoroutine
import org.speaksimpleapp.feature.auth.data.exception.AuthDataException
import org.speaksimpleapp.feature.auth.data.platform.IosAuthPlatformBridge
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IosGoogleIdentityProvider(
    private val bridge: IosAuthPlatformBridge,
) : GoogleIdentityProvider {
    override suspend fun signIn(nonce: String): GoogleIdToken? =
        suspendCancellableCoroutine { continuation ->
            bridge.signInWithGoogle(nonce) { token, error ->
                when {
                    !continuation.isActive -> Unit
                    token != null -> continuation.resume(GoogleIdToken(token))
                    error == CANCELLATION_ERROR -> continuation.resume(null)
                    else -> continuation.resumeWithException(AuthDataException())
                }
            }
        }

    override suspend fun signOut() = bridge.signOutFromGoogle()

    private companion object {
        const val CANCELLATION_ERROR = "cancelled"
    }
}
