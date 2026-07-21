package org.speaksimpleapp.feature.auth.data.mapper

import org.speaksimpleapp.feature.auth.data.local.entity.StoredSessionDb
import org.speaksimpleapp.feature.auth.data.remote.entity.AuthSessionResponse
import org.speaksimpleapp.feature.auth.domain.entity.AuthSession

internal fun AuthSessionResponse.toDomain() = AuthSession(
    accessToken = accessToken,
    refreshToken = refreshToken,
    accessTokenExpiresAtEpochSeconds = accessTokenExpiresAtEpochSeconds,
    user = user.toDomain(),
)

internal fun StoredSessionDb.toDomain() = AuthSession(
    accessToken = accessToken,
    refreshToken = refreshToken,
    accessTokenExpiresAtEpochSeconds = accessTokenExpiresAtEpochSeconds,
    user = user.toDomain(),
)

internal fun AuthSession.toDb() = StoredSessionDb(
    accessToken = accessToken,
    refreshToken = refreshToken,
    accessTokenExpiresAtEpochSeconds = accessTokenExpiresAtEpochSeconds,
    user = user.toDb(),
)
