package org.speaksimpleapp.feature.auth.data.remote.entity

internal sealed interface CurrentUserResponse {
    data class Success(val user: UserResponse) : CurrentUserResponse
    data object Unauthorized : CurrentUserResponse
}
