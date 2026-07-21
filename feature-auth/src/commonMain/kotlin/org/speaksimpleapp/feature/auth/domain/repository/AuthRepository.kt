package org.speaksimpleapp.feature.auth.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.feature.auth.domain.entity.LoginResult
import org.speaksimpleapp.feature.auth.domain.entity.SessionState

interface AuthRepository {

    val sessionState: StateFlow<SessionState>

    suspend fun restoreSession()

    suspend fun loginWithGoogle(): LoginResult

    suspend fun logout()

    suspend fun deleteAccount()
}
