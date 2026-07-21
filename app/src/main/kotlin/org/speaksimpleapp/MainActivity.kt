package org.speaksimpleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext

class MainActivity : ComponentActivity() {
    private val appContainer: AppContainer
        get() = (application as SpeakSimpleApplication).container

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val rootComponent = appContainer.rootComponentFactory(
            componentContext = defaultComponentContext(),
        )

        setContent {
            App(rootComponent = rootComponent)
        }
    }

    override fun onStart() {
        super.onStart()
        appContainer.activityProvider.attach(this)
    }

    override fun onStop() {
        appContainer.activityProvider.detach(this)
        super.onStop()
    }
}
