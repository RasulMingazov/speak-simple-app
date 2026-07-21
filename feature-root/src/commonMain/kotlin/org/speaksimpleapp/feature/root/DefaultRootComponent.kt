package org.speaksimpleapp.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.StateFlow
import org.speaksimpleapp.feature.auth.presentation.LoginComponent
import org.speaksimpleapp.feature.main.presentation.MainComponent

internal class DefaultRootComponent(
    componentContext: ComponentContext,
    modelFactory: RootModel.Factory,
    loginComponentFactory: LoginComponent.Factory,
    mainComponentFactory: MainComponent.Factory,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = null,
        initialConfiguration = Configuration.Bootstrap,
        childFactory = { configuration, childContext ->
            when (configuration) {
                Configuration.Bootstrap -> RootComponent.Child.Bootstrap
                Configuration.Login -> RootComponent.Child.Login(
                    component = loginComponentFactory(childContext),
                )
                Configuration.Main -> RootComponent.Child.Main(
                    component = mainComponentFactory(childContext),
                )
            }
        },
    )

    private val model: RootModel = instanceKeeper.getOrCreate(
        key = "RootModel",
    ) {
        modelFactory(::navigateTo)
    }

    override val isBootstrapping: StateFlow<Boolean> = model.isBootstrapping

    private fun navigateTo(destination: RootModel.Destination) {
        navigation.replaceAll(
            when (destination) {
                RootModel.Destination.BOOTSTRAP -> Configuration.Bootstrap
                RootModel.Destination.LOGIN -> Configuration.Login
                RootModel.Destination.MAIN -> Configuration.Main
            },
        )
    }

    private enum class Configuration {
        Bootstrap,
        Login,
        Main,
    }

    class Factory(
        private val modelFactory: RootModel.Factory,
        private val loginComponentFactory: LoginComponent.Factory,
        private val mainComponentFactory: MainComponent.Factory,
    ) : RootComponent.Factory {
        override fun invoke(componentContext: ComponentContext): RootComponent =
            DefaultRootComponent(
                componentContext = componentContext,
                modelFactory = modelFactory,
                loginComponentFactory = loginComponentFactory,
                mainComponentFactory = mainComponentFactory,
            )
    }
}
