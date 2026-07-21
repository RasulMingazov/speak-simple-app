package org.speaksimpleapp.feature.auth.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.speaksimpleapp.feature.auth.data.exception.AuthDataException
import org.speaksimpleapp.feature.auth.data.identity.GoogleIdToken
import org.speaksimpleapp.feature.auth.data.identity.GoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.local.AuthSessionLocalDataSource
import org.speaksimpleapp.feature.auth.data.local.entity.StoredSessionDb
import org.speaksimpleapp.feature.auth.data.mapper.toDb
import org.speaksimpleapp.feature.auth.data.platform.entity.DevicePlatform
import org.speaksimpleapp.feature.auth.data.remote.AuthRemoteDataSource
import org.speaksimpleapp.feature.auth.data.remote.entity.AuthSessionResponse
import org.speaksimpleapp.feature.auth.data.remote.entity.CurrentUserResponse
import org.speaksimpleapp.feature.auth.data.remote.entity.GoogleAuthChallengeResponse
import org.speaksimpleapp.feature.auth.data.remote.entity.GoogleLoginRequest
import org.speaksimpleapp.feature.auth.data.remote.entity.UserResponse
import org.speaksimpleapp.feature.auth.domain.entity.AuthSession
import org.speaksimpleapp.feature.auth.domain.entity.AuthUser
import org.speaksimpleapp.feature.auth.domain.entity.LoginResult
import org.speaksimpleapp.feature.auth.domain.entity.SessionState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class DefaultAuthRepositoryTest {

    @Test
    fun loginPersistsAuthenticatedSession() = runTest {
        val fixture = Fixture()

        val result = fixture.repository.loginWithGoogle()

        assertEquals(LoginResult.Success, result)
        assertEquals(Session.toDb(), fixture.localDataSource.session)
        assertEquals(SessionState.SignedIn(Session), fixture.repository.sessionState.value)
        assertEquals(DevicePlatform.ANDROID.name, fixture.remoteDataSource.loginRequest?.device?.platform)
    }

    @Test
    fun cancelledGoogleSignInDoesNotCreateSession() = runTest {
        val fixture = Fixture().apply {
            identityProvider.idToken = null
        }

        val result = fixture.repository.loginWithGoogle()

        assertEquals(LoginResult.Cancelled, result)
        assertNull(fixture.localDataSource.session)
        assertNull(fixture.remoteDataSource.loginRequest)
    }

    @Test
    fun loginFailureIsCollapsedToGenericError() = runTest {
        val fixture = Fixture().apply {
            remoteDataSource.loginError = AuthDataException()
        }

        val result = fixture.repository.loginWithGoogle()

        assertEquals(LoginResult.Error, result)
        assertNull(fixture.localDataSource.session)
    }

    @Test
    fun loginDoesNotConsumeCoroutineCancellation() = runTest {
        val fixture = Fixture().apply {
            remoteDataSource.loginError = CancellationException("cancelled")
        }

        assertFailsWith<CancellationException> {
            fixture.repository.loginWithGoogle()
        }
    }

    @Test
    fun restoreUpdatesCachedUser() = runTest {
        val updatedUser = User.copy(displayName = "Updated name")
        val fixture = Fixture(cachedSession = Session).apply {
            remoteDataSource.currentUserResponse = CurrentUserResponse.Success(updatedUser.toResponse())
        }

        fixture.repository.restoreSession()

        val expectedSession = Session.copy(user = updatedUser)
        assertEquals(expectedSession.toDb(), fixture.localDataSource.session)
        assertEquals(SessionState.SignedIn(expectedSession), fixture.repository.sessionState.value)
    }

    @Test
    fun restoreKeepsCachedSessionWhenBackendIsUnavailable() = runTest {
        val fixture = Fixture(cachedSession = Session).apply {
            remoteDataSource.currentUserError = AuthDataException()
        }

        fixture.repository.restoreSession()

        assertEquals(SessionState.SignedIn(Session), fixture.repository.sessionState.value)
        assertEquals(Session.toDb(), fixture.localDataSource.session)
    }

    @Test
    fun failedRefreshDiscardsUnauthorizedSession() = runTest {
        val fixture = Fixture(cachedSession = Session).apply {
            remoteDataSource.currentUserResponse = CurrentUserResponse.Unauthorized
            remoteDataSource.refreshError = AuthDataException()
        }

        fixture.repository.restoreSession()

        assertEquals(SessionState.SignedOut, fixture.repository.sessionState.value)
        assertNull(fixture.localDataSource.session)
    }

    private class Fixture(cachedSession: AuthSession? = null) {
        val remoteDataSource = FakeAuthRemoteDataSource()
        val localDataSource = FakeAuthSessionLocalDataSource(cachedSession?.toDb())
        val identityProvider = FakeGoogleIdentityProvider()
        val repository = DefaultAuthRepository(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            googleIdentityProvider = identityProvider,
            devicePlatform = DevicePlatform.ANDROID,
        )
    }

    private class FakeAuthRemoteDataSource : AuthRemoteDataSource {
        var loginRequest: GoogleLoginRequest? = null
        var loginError: Throwable? = null
        var currentUserResponse: CurrentUserResponse = CurrentUserResponse.Success(User.toResponse())
        var currentUserError: Throwable? = null
        var refreshError: Throwable? = null

        override suspend fun createChallenge() = GoogleAuthChallengeResponse(
            challengeId = "challenge-id",
            nonce = "nonce",
        )

        override suspend fun login(request: GoogleLoginRequest): AuthSessionResponse {
            loginError?.let { throw it }
            loginRequest = request
            return Session.toResponse()
        }

        override suspend fun refresh(refreshToken: String): AuthSessionResponse {
            refreshError?.let { throw it }
            return Session.toResponse()
        }

        override suspend fun logout(refreshToken: String) = Unit

        override suspend fun getCurrentUser(accessToken: String): CurrentUserResponse {
            currentUserError?.let { throw it }
            return currentUserResponse
        }

        override suspend fun deleteAccount(accessToken: String) = Unit
    }

    private class FakeAuthSessionLocalDataSource(
        var session: StoredSessionDb?,
    ) : AuthSessionLocalDataSource {
        override suspend fun read(): StoredSessionDb? = session

        override suspend fun write(session: StoredSessionDb) {
            this.session = session
        }

        override suspend fun clear() {
            session = null
        }
    }

    private class FakeGoogleIdentityProvider : GoogleIdentityProvider {
        var idToken: GoogleIdToken? = GoogleIdToken("google-token")

        override suspend fun signIn(nonce: String): GoogleIdToken? = idToken

        override suspend fun signOut() = Unit
    }

    private companion object {
        val User = AuthUser(
            id = "user-id",
            displayName = "Rasul",
            email = "rasul@example.com",
            avatarUrl = null,
        )
        val Session = AuthSession(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            accessTokenExpiresAtEpochSeconds = 1_800_000_000,
            user = User,
        )

        fun AuthUser.toResponse() = UserResponse(
            id = id,
            displayName = displayName,
            email = email,
            avatarUrl = avatarUrl,
        )

        fun AuthSession.toResponse() = AuthSessionResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresAtEpochSeconds = accessTokenExpiresAtEpochSeconds,
            user = user.toResponse(),
        )
    }
}
