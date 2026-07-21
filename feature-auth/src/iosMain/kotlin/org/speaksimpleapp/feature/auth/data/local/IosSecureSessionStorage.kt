package org.speaksimpleapp.feature.auth.data.local

import kotlinx.coroutines.suspendCancellableCoroutine
import org.speaksimpleapp.feature.auth.data.exception.AuthDataException
import org.speaksimpleapp.feature.auth.data.platform.IosAuthPlatformBridge
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IosSecureSessionStorage(
    private val bridge: IosAuthPlatformBridge,
) : SecureSessionStorage {
    override suspend fun read(): String? = suspendCancellableCoroutine { continuation ->
        bridge.readSecureSession { value, error ->
            if (!continuation.isActive) return@readSecureSession
            if (error == null) continuation.resume(value)
            else continuation.resumeWithException(AuthDataException())
        }
    }

    override suspend fun write(value: String): Unit = suspendCancellableCoroutine { continuation ->
        bridge.writeSecureSession(value) { error ->
            if (!continuation.isActive) return@writeSecureSession
            if (error == null) continuation.resume(Unit)
            else continuation.resumeWithException(AuthDataException())
        }
    }

    override suspend fun clear(): Unit = suspendCancellableCoroutine { continuation ->
        bridge.clearSecureSession { error ->
            if (!continuation.isActive) return@clearSecureSession
            if (error == null) continuation.resume(Unit)
            else continuation.resumeWithException(AuthDataException())
        }
    }
}
