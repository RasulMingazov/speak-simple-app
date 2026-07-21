package org.speaksimpleapp.feature.auth.data.remote.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class GoogleLoginRequest(
    val challengeId: String,
    val idToken: String,
    val device: DeviceRequest,
)
