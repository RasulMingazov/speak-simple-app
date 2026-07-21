package org.speaksimpleapp.feature.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.speaksimpleapp.core.design.theme.SpeakSimpleTheme

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoginLightPreview() {
    SpeakSimpleTheme(darkTheme = false) {
        LoginScreen(
            state = DefaultLoginUiStateMapper(
                LoginModel.DataState(hasError = true),
            ),
            onGoogleLoginClicked = {},
            onErrorDismissed = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoginDarkPreview() {
    SpeakSimpleTheme(darkTheme = true) {
        LoginScreen(
            state = DefaultLoginUiStateMapper(LoginModel.DataState()),
            onGoogleLoginClicked = {},
            onErrorDismissed = {},
        )
    }
}
