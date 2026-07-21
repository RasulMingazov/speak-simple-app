package org.speaksimpleapp.feature.auth.di

import org.speaksimpleapp.feature.auth.data.platform.entity.DevicePlatform

internal data class AuthRuntimeConfig(
    val apiBaseUrl: String,
    val devicePlatform: DevicePlatform,
)
