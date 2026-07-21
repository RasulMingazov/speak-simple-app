package org.speaksimpleapp.feature.auth.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

internal actual fun platformHttpClientEngine(): HttpClientEngine = Android.create()
