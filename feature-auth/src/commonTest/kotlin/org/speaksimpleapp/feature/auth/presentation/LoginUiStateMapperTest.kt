package org.speaksimpleapp.feature.auth.presentation

import speak_simple_app.feature_auth.generated.resources.Res
import speak_simple_app.feature_auth.generated.resources.login_brand_mark
import speak_simple_app.feature_auth.generated.resources.login_error_message
import speak_simple_app.feature_auth.generated.resources.login_error_title
import speak_simple_app.feature_auth.generated.resources.login_google
import speak_simple_app.feature_auth.generated.resources.login_privacy
import speak_simple_app.feature_auth.generated.resources.login_subtitle
import speak_simple_app.feature_auth.generated.resources.login_title
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class LoginUiStateMapperTest {
    private val mapper: LoginUiStateMapper = DefaultLoginUiStateMapper

    @Test
    fun `idle state contains login content and no error`() {
        val uiState = mapper(LoginModel.DataState())

        assertEquals(Res.string.login_brand_mark, uiState.brandMark)
        assertEquals(Res.string.login_title, uiState.title)
        assertEquals(Res.string.login_subtitle, uiState.subtitle)
        assertEquals(Res.string.login_google, uiState.googleButtonTitle)
        assertEquals(Res.string.login_privacy, uiState.privacyMessage)
        assertFalse(uiState.isGoogleSignInInProgress)
        assertNull(uiState.error)
    }

    @Test
    fun `error state maps to generic localized error resources`() {
        val error = mapper(LoginModel.DataState(hasError = true)).error

        assertEquals(Res.string.login_error_title, error?.title)
        assertEquals(Res.string.login_error_message, error?.message)
    }
}
