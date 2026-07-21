package org.speaksimpleapp.feature.auth.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class StoredUserDb(
    val id: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null,
)
