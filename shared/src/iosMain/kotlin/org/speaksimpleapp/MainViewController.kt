package org.speaksimpleapp

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.speaksimpleapp.feature.auth.data.identity.GoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.identity.IosGoogleIdentityProvider
import org.speaksimpleapp.feature.auth.data.local.IosSecureSessionStorage
import org.speaksimpleapp.feature.auth.data.local.SecureSessionStorage
import org.speaksimpleapp.feature.auth.data.platform.entity.DevicePlatform
import org.speaksimpleapp.feature.auth.data.platform.IosAuthPlatformBridge
import org.speaksimpleapp.feature.auth.di.AuthPlatformDependencies
import org.speaksimpleapp.feature.auth.di.AuthRuntimeConfig
import org.speaksimpleapp.feature.auth.di.DefaultAuthContainer
import org.speaksimpleapp.feature.root.di.DefaultRootContainer
import platform.UIKit.UIViewController

interface IosAuthBridge {
    fun signInWithGoogle(nonce: String, completion: (idToken: String?, errorCode: String?) -> Unit)
    fun signOutFromGoogle()
    fun readSecureSession(completion: (value: String?, error: String?) -> Unit)
    fun writeSecureSession(value: String, completion: (error: String?) -> Unit)
    fun clearSecureSession(completion: (error: String?) -> Unit)
}

fun MainViewController(
    apiBaseUrl: String,
    authBridge: IosAuthBridge,
): UIViewController {
    val platformBridge = object : IosAuthPlatformBridge {
        override fun signInWithGoogle(nonce: String, completion: (String?, String?) -> Unit) =
            authBridge.signInWithGoogle(nonce, completion)
        override fun signOutFromGoogle() = authBridge.signOutFromGoogle()
        override fun readSecureSession(completion: (String?, String?) -> Unit) =
            authBridge.readSecureSession(completion)
        override fun writeSecureSession(value: String, completion: (String?) -> Unit) =
            authBridge.writeSecureSession(value, completion)
        override fun clearSecureSession(completion: (String?) -> Unit) =
            authBridge.clearSecureSession(completion)
    }
    val authContainer = DefaultAuthContainer(
        config = AuthRuntimeConfig(apiBaseUrl, DevicePlatform.IOS),
        platform = object : AuthPlatformDependencies {
            override val googleIdentityProvider: GoogleIdentityProvider =
                IosGoogleIdentityProvider(platformBridge)
            override val secureSessionStorage: SecureSessionStorage =
                IosSecureSessionStorage(platformBridge)
        },
    )
    val rootComponent = DefaultRootContainer(authContainer).rootComponentFactory(
        componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry())
    )

    return ComposeUIViewController {
        App(rootComponent = rootComponent)
    }
}
