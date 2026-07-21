package org.speaksimpleapp.feature.auth.data.platform

/**
 * Implemented by the iOS host with GoogleSignIn and Keychain Services.
 */
interface IosAuthPlatformBridge {
    fun signInWithGoogle(nonce: String, completion: (idToken: String?, errorCode: String?) -> Unit)
    fun signOutFromGoogle()
    fun readSecureSession(completion: (value: String?, error: String?) -> Unit)
    fun writeSecureSession(value: String, completion: (error: String?) -> Unit)
    fun clearSecureSession(completion: (error: String?) -> Unit)
}
