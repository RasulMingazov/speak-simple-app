package org.speaksimpleapp.feature.auth.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.speaksimpleapp.feature.auth.data.exception.AuthDataException
import org.speaksimpleapp.feature.auth.data.identity.GoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.local.AuthSessionLocalDataSource
import org.speaksimpleapp.feature.auth.data.mapper.toDb
import org.speaksimpleapp.feature.auth.data.mapper.toDomain
import org.speaksimpleapp.feature.auth.data.platform.entity.DevicePlatform
import org.speaksimpleapp.feature.auth.data.remote.AuthRemoteDataSource
import org.speaksimpleapp.feature.auth.data.remote.entity.CurrentUserResponse
import org.speaksimpleapp.feature.auth.data.remote.entity.DeviceRequest
import org.speaksimpleapp.feature.auth.data.remote.entity.GoogleLoginRequest
import org.speaksimpleapp.feature.auth.domain.entity.AuthSession
import org.speaksimpleapp.feature.auth.domain.entity.LoginResult
import org.speaksimpleapp.feature.auth.domain.entity.SessionState
import org.speaksimpleapp.feature.auth.domain.repository.AuthRepository

internal class DefaultAuthRepository(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthSessionLocalDataSource,
    private val googleIdentityProvider: GoogleIdentityProvider,
    private val devicePlatform: DevicePlatform,
) : AuthRepository {

    private val mutex = Mutex()

    private val mutableSessionState = MutableStateFlow<SessionState>(SessionState.Initializing)

    override val sessionState: StateFlow<SessionState> = mutableSessionState.asStateFlow()

    override suspend fun restoreSession() = mutex.withLock {
        val cachedSession = readCachedSession()
        if (cachedSession == null) {
            discardSession()
            return@withLock
        }

        mutableSessionState.value = SessionState.SignedIn(cachedSession)
        updateCachedSession(cachedSession)
    }

    override suspend fun loginWithGoogle(): LoginResult = mutex.withLock {
        resultOrNull(::login) ?: LoginResult.Error
    }

    override suspend fun logout() = mutex.withLock {
        val session = currentSession()
        discardSession()
        if (session != null) resultOrNull {
            remoteDataSource.logout(session.refreshToken)
        }
        resultOrNull(googleIdentityProvider::signOut)
        Unit
    }

    override suspend fun deleteAccount() = mutex.withLock {
        val session = currentSession() ?: throw AuthDataException()
        remoteDataSource.deleteAccount(session.accessToken)
        discardSession()
        resultOrNull(googleIdentityProvider::signOut)
        Unit
    }

    private suspend fun login(): LoginResult {
        val challenge = remoteDataSource.createChallenge()
        val idToken = googleIdentityProvider.signIn(challenge.nonce)
            ?: return LoginResult.Cancelled
        val session = remoteDataSource.login(
            GoogleLoginRequest(
                challengeId = challenge.challengeId,
                idToken = idToken.value,
                device = DeviceRequest(devicePlatform.name),
            ),
        ).toDomain()
        persistSession(session)
        return LoginResult.Success
    }

    private suspend fun readCachedSession(): AuthSession? =
        resultOrNull { localDataSource.read()?.toDomain() }

    private suspend fun updateCachedSession(session: AuthSession) {
        when (val result = resultOrNull { remoteDataSource.getCurrentUser(session.accessToken) }) {
            is CurrentUserResponse.Success -> resultOrNull {
                persistSession(
                    session.copy(user = result.user.toDomain()),
                )
            }
            CurrentUserResponse.Unauthorized -> refreshSession(session.refreshToken)
            null -> Unit // Keep the cached session while the backend is temporarily unavailable.
        }
    }

    private suspend fun refreshSession(refreshToken: String) {
        val refreshedSession = resultOrNull {
            remoteDataSource.refresh(refreshToken).toDomain()
        }
        if (refreshedSession == null || resultOrNull { persistSession(refreshedSession) } == null) {
            discardSession()
        }
    }

    private suspend fun persistSession(session: AuthSession) {
        localDataSource.write(session.toDb())
        mutableSessionState.value = SessionState.SignedIn(session)
    }

    private suspend fun discardSession() {
        mutableSessionState.value = SessionState.SignedOut
        resultOrNull(localDataSource::clear)
    }

    private fun currentSession(): AuthSession? =
        (mutableSessionState.value as? SessionState.SignedIn)?.session

    private suspend inline fun <T> resultOrNull(block: suspend () -> T): T? = try {
        block()
    } catch (error: CancellationException) {
        throw error
    } catch (_: Throwable) {
        null
    }
}
