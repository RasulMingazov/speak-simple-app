package org.speaksimpleapp.feature.root

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.feature.auth.di.AuthSessionController
import org.speaksimpleapp.feature.auth.domain.entity.AuthSession
import org.speaksimpleapp.feature.auth.domain.entity.AuthUser
import org.speaksimpleapp.feature.auth.domain.entity.SessionState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RootModelTest {
    @Test
    fun `restores session and routes from bootstrap to login`() = runTest {
        val sessionController = FakeAuthSessionController(
            restoredState = SessionState.SignedOut,
        )
        val destinations = mutableListOf<RootModel.Destination>()
        val model = RootModel(
            authSessionController = sessionController,
            onDestinationChanged = destinations::add,
            coroutineDispatchers = TestDispatchers(StandardTestDispatcher(testScheduler)),
        )

        assertTrue(model.isBootstrapping.value)
        runCurrent()

        assertEquals(1, sessionController.restoreInvocationCount)
        assertEquals(listOf(RootModel.Destination.LOGIN), destinations)
        assertFalse(model.isBootstrapping.value)
        model.onDestroy()
    }

    @Test
    fun `routes to main once for equivalent signed in states`() = runTest {
        val sessionController = FakeAuthSessionController()
        val destinations = mutableListOf<RootModel.Destination>()
        val model = RootModel(
            authSessionController = sessionController,
            onDestinationChanged = destinations::add,
            coroutineDispatchers = TestDispatchers(StandardTestDispatcher(testScheduler)),
        )
        runCurrent()

        sessionController.state.value = SessionState.SignedIn(Session)
        runCurrent()
        sessionController.state.value = SessionState.SignedIn(
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

private class FakeAuthSessionController(
    private val restoredState: SessionState? = null,
) : AuthSessionController {
    override val state = MutableStateFlow<SessionState>(SessionState.Initializing)

    var restoreInvocationCount: Int = 0
        private set

    override suspend fun restore() {
        restoreInvocationCount += 1
        restoredState?.let { state.value = it }
    }
}

private class TestDispatchers(
    override val main: CoroutineDispatcher,
) : CoroutineDispatchers
