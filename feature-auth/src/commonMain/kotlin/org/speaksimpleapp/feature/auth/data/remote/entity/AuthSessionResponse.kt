package org.speaksimpleapp.feature.auth.data.remote.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class AuthSessionResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAtEpochSeconds: Long,
    val user: UserResponse,
)
