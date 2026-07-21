package org.speaksimpleapp.feature.auth.domain.entity

sealed interface LoginResult {
    data object Success : LoginResult
    data object Cancelled : LoginResult
    data object Error : LoginResult
}
