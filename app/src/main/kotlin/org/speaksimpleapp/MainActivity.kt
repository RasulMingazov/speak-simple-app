package org.speaksimpleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import org.speaksimpleapp.feature.root.di.DefaultRootContainer

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val rootComponent = DefaultRootContainer().rootComponentFactory(
            componentContext = defaultComponentContext()
        )

        setContent {
            App(rootComponent = rootComponent)
        }
    }
}
