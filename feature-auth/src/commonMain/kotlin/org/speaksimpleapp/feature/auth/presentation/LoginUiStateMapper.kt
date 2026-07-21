package org.speaksimpleapp.feature.auth.presentation

import org.speaksimpleapp.feature.auth.presentation.LoginModel.DataState
import speak_simple_app.feature_auth.generated.resources.Res
import speak_simple_app.feature_auth.generated.resources.login_brand_mark
import speak_simple_app.feature_auth.generated.resources.login_error_icon_description
import speak_simple_app.feature_auth.generated.resources.login_error_message
import speak_simple_app.feature_auth.generated.resources.login_error_title
import speak_simple_app.feature_auth.generated.resources.login_google
import speak_simple_app.feature_auth.generated.resources.login_google_icon_description
import speak_simple_app.feature_auth.generated.resources.login_privacy
import speak_simple_app.feature_auth.generated.resources.login_subtitle
import speak_simple_app.feature_auth.generated.resources.login_title

internal interface LoginUiStateMapper {
    operator fun invoke(dataState: DataState): LoginUiState
}

internal object DefaultLoginUiStateMapper : LoginUiStateMapper {
    override fun invoke(dataState: DataState): LoginUiState = LoginUiState(
        brandMark = Res.string.login_brand_mark,
        title = Res.string.login_title,
        subtitle = Res.string.login_subtitle,
        googleButtonTitle = Res.string.login_google,
        googleIconContentDescription = Res.string.login_google_icon_description,
        privacyMessage = Res.string.login_privacy,
        isGoogleSignInInProgress = dataState.isGoogleSignInInProgress,
        error = if (dataState.hasError) {
            LoginErrorUiState(
                title = Res.string.login_error_title,
                message = Res.string.login_error_message,
                iconContentDescription = Res.string.login_error_icon_description,
            )
        } else {
            null
        },
    )
}
