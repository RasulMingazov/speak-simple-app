package org.speaksimpleapp.feature.auth.data.local

internal interface SecureSessionStorage {
    suspend fun read(): String?
    suspend fun write(value: String)
    suspend fun clear()
}
