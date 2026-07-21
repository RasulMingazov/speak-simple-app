package org.speaksimpleapp.feature.auth.domain.entity

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAtEpochSeconds: Long,
    val user: AuthUser,
)
