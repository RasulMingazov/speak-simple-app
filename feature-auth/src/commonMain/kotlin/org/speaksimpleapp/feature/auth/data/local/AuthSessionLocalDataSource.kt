package org.speaksimpleapp.feature.auth.data.local

import kotlinx.serialization.json.Json
import org.speaksimpleapp.feature.auth.data.local.entity.StoredSessionDb

internal interface AuthSessionLocalDataSource {
    suspend fun read(): StoredSessionDb?
    suspend fun write(session: StoredSessionDb)
    suspend fun clear()
}

internal class DefaultAuthSessionLocalDataSource(
    private val storage: SecureSessionStorage,
    private val json: Json = Json { ignoreUnknownKeys = true; explicitNulls = false },
) : AuthSessionLocalDataSource {
    override suspend fun read(): StoredSessionDb? {
        val storedValue = storage.read() ?: return null
        val session = runCatching {
            json.decodeFromString(StoredSessionDb.serializer(), storedValue)
        }.getOrNull()
        if (session == null) storage.clear()
        return session
    }

    override suspend fun write(session: StoredSessionDb) {
        storage.write(json.encodeToString(StoredSessionDb.serializer(), session))
    }

    override suspend fun clear() = storage.clear()
}
