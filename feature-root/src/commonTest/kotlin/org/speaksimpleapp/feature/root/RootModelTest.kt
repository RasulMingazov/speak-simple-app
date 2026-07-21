package org.speaksimpleapp.feature.root

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.speaksimpleapp.core.test.testDispatchers
import org.speaksimpleapp.feature.auth.domain.entity.AuthSession
import org.speaksimpleapp.feature.auth.domain.entity.AuthUser
import org.speaksimpleapp.feature.auth.domain.entity.SessionState
import org.speaksimpleapp.feature.auth.domain.usecase.ObserveSessionUseCase
import org.speaksimpleapp.feature.auth.domain.usecase.RestoreSessionUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RootModelTest {
    @Test
    fun `restores session and routes from bootstrap to login`() = runTest {
        val sessionState = MutableStateFlow<SessionState>(SessionState.Initializing)
        val restoreSessionUseCase = FakeRestoreSessionUseCase {
            sessionState.value = SessionState.SignedOut
        }
        val destinations = mutableListOf<RootModel.Destination>()
        val model = RootModel(
            observeSessionUseCase = FakeObserveSessionUseCase(sessionState),
            restoreSessionUseCase = restoreSessionUseCase,
            onDestinationChanged = destinations::add,
            coroutineDispatchers = testDispatchers(),
        )

        assertTrue(model.isBootstrapping.value)
        runCurrent()

        assertEquals(1, restoreSessionUseCase.invocationCount)
        assertEquals(listOf(RootModel.Destination.LOGIN), destinations)
        assertFalse(model.isBootstrapping.value)
        model.onDestroy()
    }

    @Test
    fun `routes to main once for equivalent signed in states`() = runTest {
        val sessionState = MutableStateFlow<SessionState>(SessionState.Initializing)
        val destinations = mutableListOf<RootModel.Destination>()
        val model = RootModel(
            observeSessionUseCase = FakeObserveSessionUseCase(sessionState),
            restoreSessionUseCase = FakeRestoreSessionUseCase(),
            onDestinationChanged = destinations::add,
            coroutineDispatchers = testDispatchers(),
        )
        runCurrent()

        sessionState.value = SessionState.SignedIn(Session)
        runCurrent()
        sessionState.value = SessionState.SignedIn(
            Session.copy(accessToken = "updated-access-token"),
        )
        runCurrent()

        assertEquals(listOf(RootModel.Destination.MAIN), destinations)
        assertFalse(model.isBootstrapping.value)
        model.onDestroy()
    }

    private companion object {
        val Session = AuthSession(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            accessTokenExpiresAtEpochSeconds = 1L,
            user = AuthUser(
                id = "user-id",
                displayName = "User",
                email = "user@example.com",
                avatarUrl = null,
            ),
        )
    }
}

private class FakeObserveSessionUseCase(
    private val state: MutableStateFlow<SessionState>,
) : ObserveSessionUseCase {
    override fun invoke() = state
}

private class FakeRestoreSessionUseCase(
    private val restore: suspend () -> Unit = {},
) : RestoreSessionUseCase {
    var invocationCount: Int = 0
        private set

    override suspend fun invoke() {
        invocationCount += 1
        restore()
    }
}
