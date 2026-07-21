package org.speaksimpleapp.feature.auth.data.remote.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class DeviceRequest(
    val platform: String,
)
