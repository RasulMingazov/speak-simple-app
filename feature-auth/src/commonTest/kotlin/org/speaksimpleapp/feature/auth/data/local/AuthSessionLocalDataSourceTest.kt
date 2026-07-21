package org.speaksimpleapp.feature.auth.data.local

import kotlinx.coroutines.test.runTest
import org.speaksimpleapp.feature.auth.data.local.entity.StoredSessionDb
import org.speaksimpleapp.feature.auth.data.local.entity.StoredUserDb
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthSessionLocalDataSourceTest {

    @Test
    fun writesAndReadsSession() = runTest {
        val storage = FakeSecureSessionStorage()
        val dataSource = DefaultAuthSessionLocalDataSource(storage)

        dataSource.write(Session)

        assertEquals(Session, dataSource.read())
    }

    @Test
    fun removesCorruptedSession() = runTest {
        val storage = FakeSecureSessionStorage(value = "not-json")
        val dataSource = DefaultAuthSessionLocalDataSource(storage)

        val result = dataSource.read()

        assertNull(result)
        assertNull(storage.value)
    }

    private class FakeSecureSessionStorage(
        var value: String? = null,
    ) : SecureSessionStorage {
        override suspend fun read(): String? = value

        override suspend fun write(value: String) {
            this.value = value
        }

        override suspend fun clear() {
            value = null
        }
    }

    private companion object {
        val Session = StoredSessionDb(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            accessTokenExpiresAtEpochSeconds = 1_800_000_000,
            user = StoredUserDb(
                id = "user-id",
                displayName = "Rasul",
                email = "rasul@example.com",
                avatarUrl = null,
            ),
        )
    }
}
