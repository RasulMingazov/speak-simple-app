package org.speaksimpleapp

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.create
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.pause
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.start
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.cinterop.ExperimentalForeignApi
import org.speaksimpleapp.feature.auth.data.platform.IosAuthPlatformBridge
import org.speaksimpleapp.feature.auth.di.createIosAuthContainer
import org.speaksimpleapp.feature.root.di.createRootComponentFactory
import platform.Foundation.NSCoder
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController

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
    val authContainer = createIosAuthContainer(
        apiBaseUrl = apiBaseUrl,
        bridge = platformBridge,
    )
    val lifecycle = LifecycleRegistry()
    val rootComponent = createRootComponentFactory(authContainer)(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
    )
    val contentViewController = ComposeUIViewController {
        DisposableEffect(lifecycle) {
            onDispose(lifecycle::destroy)
        }
        App(rootComponent = rootComponent)
    }

    return LifecycleViewController(
        contentViewController = contentViewController,
        lifecycle = lifecycle,
    )
}

@OptIn(ExperimentalForeignApi::class)
private class LifecycleViewController(
    private val contentViewController: UIViewController,
    private val lifecycle: LifecycleRegistry,
) : UIViewController(nibName = null, bundle = null) {

    @Suppress("UNUSED_PARAMETER")
    constructor(coder: NSCoder) : this(
        contentViewController = UIViewController(),
        lifecycle = LifecycleRegistry(),
    )

    override fun viewDidLoad() {
        super.viewDidLoad()
        lifecycle.create()
        addChildViewController(contentViewController)
        val contentView = contentViewController.view
        contentView.setFrame(view.bounds)
        contentView.autoresizingMask =
            UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
        view.addSubview(contentView)
        contentViewController.didMoveToParentViewController(this)
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        lifecycle.start()
    }

    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)
        lifecycle.resume()
    }

    override fun viewWillDisappear(animated: Boolean) {
        lifecycle.pause()
        super.viewWillDisappear(animated)
    }

    override fun viewDidDisappear(animated: Boolean) {
        lifecycle.stop()
        super.viewDidDisappear(animated)
    }

}
