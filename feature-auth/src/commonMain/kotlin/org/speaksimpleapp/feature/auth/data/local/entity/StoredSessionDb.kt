package org.speaksimpleapp.feature.auth.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class StoredSessionDb(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAtEpochSeconds: Long,
    val user: StoredUserDb,
)
