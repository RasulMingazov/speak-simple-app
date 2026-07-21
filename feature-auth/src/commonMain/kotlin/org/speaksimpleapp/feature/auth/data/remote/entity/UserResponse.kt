package org.speaksimpleapp.feature.auth.data.remote.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class UserResponse(
    val id: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null,
)
