package org.speaksimpleapp.feature.auth.data.mapper

import org.speaksimpleapp.feature.auth.data.local.entity.StoredUserDb
import org.speaksimpleapp.feature.auth.data.remote.entity.UserResponse
import org.speaksimpleapp.feature.auth.domain.entity.AuthUser

internal fun UserResponse.toDomain() = AuthUser(
    id = id,
    displayName = displayName,
    email = email,
    avatarUrl = avatarUrl,
)

internal fun StoredUserDb.toDomain() = AuthUser(
    id = id,
    displayName = displayName,
    email = email,
    avatarUrl = avatarUrl,
)

internal fun AuthUser.toDb() = StoredUserDb(
    id = id,
    displayName = displayName,
    email = email,
    avatarUrl = avatarUrl,
)
