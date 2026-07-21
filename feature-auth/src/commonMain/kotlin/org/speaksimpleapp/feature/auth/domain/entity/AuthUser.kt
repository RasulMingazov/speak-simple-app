package org.speaksimpleapp.feature.auth.domain.entity

data class AuthUser(
    val id: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
)
