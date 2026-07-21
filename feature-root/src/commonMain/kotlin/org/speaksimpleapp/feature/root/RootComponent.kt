package org.speaksimpleapp.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.feature.auth.presentation.LoginComponent
import org.speaksimpleapp.feature.main.presentation.MainComponent

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>
    val isBootstrapping: StateFlow<Boolean>

    sealed interface Child {
        data object Bootstrap : Child
        data class Login(val component: LoginComponent) : Child
        data class Main(val component: MainComponent) : Child
    }

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): RootComponent
    }
}
