package org.speaksimpleapp.feature.auth.domain.entity

sealed interface SessionState {
    data object Initializing : SessionState
    data object SignedOut : SessionState
    data class SignedIn(val session: AuthSession) : SessionState
}
