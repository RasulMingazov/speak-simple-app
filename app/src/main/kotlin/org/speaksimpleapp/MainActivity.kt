package org.speaksimpleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext

class MainActivity : ComponentActivity() {
    private val appContainer: AppContainer
        get() = (application as SpeakSimpleApplication).container

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val rootComponent = appContainer.rootComponentFactory(
            componentContext = defaultComponentContext(),
        )
        splashScreen.setKeepOnScreenCondition {
            rootComponent.isBootstrapping.value
        }

        setContent {
            App(rootComponent = rootComponent)
        }
    }

    override fun onStart() {
        super.onStart()
        appContainer.attachActivity(this)
    }

    override fun onStop() {
        appContainer.detachActivity(this)
        super.onStop()
    }
}
