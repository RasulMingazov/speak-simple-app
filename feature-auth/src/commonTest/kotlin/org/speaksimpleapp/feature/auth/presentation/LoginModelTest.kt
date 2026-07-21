package org.speaksimpleapp.feature.auth.presentation

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.speaksimpleapp.core.test.testDispatchers
import org.speaksimpleapp.feature.auth.domain.entity.LoginResult
import org.speaksimpleapp.feature.auth.domain.usecase.LoginWithGoogleUseCase
import speak_simple_app.feature_auth.generated.resources.Res
import speak_simple_app.feature_auth.generated.resources.login_error_title
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class LoginModelTest {
    @Test
    fun `login failure shows generic error`() = runTest {
        val loginWithGoogleUseCase = FakeLoginWithGoogleUseCase(LoginResult.Error)
        val model = LoginModel(
            loginWithGoogleUseCase = loginWithGoogleUseCase,
            coroutineDispatchers = testDispatchers(),
        )

        model.dispatch(LoginComponent.Event.GoogleLoginClicked)
        advanceUntilIdle()

        assertFalse(model.uiState.value.isGoogleSignInInProgress)
        assertEquals(Res.string.login_error_title, model.uiState.value.error?.title)
        model.onDestroy()
    }

    @Test
    fun `cancelling account picker does not show an error`() = runTest {
        val loginWithGoogleUseCase = FakeLoginWithGoogleUseCase(LoginResult.Cancelled)
        val model = LoginModel(
            loginWithGoogleUseCase = loginWithGoogleUseCase,
            coroutineDispatchers = testDispatchers(),
        )

        model.dispatch(LoginComponent.Event.GoogleLoginClicked)
        advanceUntilIdle()

        assertEquals(DefaultLoginUiStateMapper(LoginModel.DataState()), model.uiState.value)
        model.onDestroy()
    }

    @Test
    fun `repeated click does not start another login while first is in progress`() = runTest {
        val pendingResult = CompletableDeferred<LoginResult>()
        val loginWithGoogleUseCase = FakeLoginWithGoogleUseCase { pendingResult.await() }
        val model = LoginModel(
            loginWithGoogleUseCase = loginWithGoogleUseCase,
            coroutineDispatchers = testDispatchers(),
        )

        model.dispatch(LoginComponent.Event.GoogleLoginClicked)
        model.dispatch(LoginComponent.Event.GoogleLoginClicked)
        runCurrent()

        assertTrue(model.uiState.value.isGoogleSignInInProgress)
        assertEquals(1, loginWithGoogleUseCase.invocationCount)

        pendingResult.complete(LoginResult.Cancelled)
        advanceUntilIdle()
        model.onDestroy()
    }
}

private class FakeLoginWithGoogleUseCase(
    private val login: suspend () -> LoginResult,
) : LoginWithGoogleUseCase {
    constructor(loginResult: LoginResult) : this(login = { loginResult })

    var invocationCount: Int = 0
        private set

    override suspend fun invoke(): LoginResult {
        invocationCount += 1
        return login()
    }
}
