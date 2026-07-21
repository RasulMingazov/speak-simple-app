package org.speaksimpleapp.feature.auth.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers
import org.speaksimpleapp.core.common.presentation.BaseModel
import org.speaksimpleapp.feature.auth.domain.entity.LoginResult
import org.speaksimpleapp.feature.auth.domain.usecase.LoginWithGoogleUseCase

internal class DefaultLoginComponent(
    componentContext: ComponentContext,
    modelFactory: LoginModel.Factory,
) : LoginComponent, ComponentContext by componentContext {

    private val model: LoginModel = instanceKeeper.getOrCreate(
        key = "LoginModel"
    ) { modelFactory() }

    override val uiState: StateFlow<LoginUiState> = model.uiState

    override fun dispatch(event: LoginComponent.Event) = model.dispatch(event)

    class Factory(
        private val modelFactory: LoginModel.Factory,
    ) : LoginComponent.Factory {

        override fun invoke(componentContext: ComponentContext): LoginComponent =
            DefaultLoginComponent(
                componentContext = componentContext,
                modelFactory = modelFactory,
            )
    }
}

internal class LoginModel(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    uiStateMapper: LoginUiStateMapper = DefaultLoginUiStateMapper,
    coroutineDispatchers: CoroutineDispatchers,
) : BaseModel(coroutineDispatchers) {

    private val dataState = MutableStateFlow(DataState())

    val uiState: StateFlow<LoginUiState> = dataState.mapState(uiStateMapper::invoke)

    fun dispatch(event: LoginComponent.Event) {
        when (event) {
            LoginComponent.Event.GoogleLoginClicked -> login()
            LoginComponent.Event.ErrorDismissed -> dataState.update { it.copy(hasError = false) }
        }
    }

    private fun login() {
        if (dataState.value.isGoogleSignInInProgress) return

        dataState.update {
            it.copy(
                isGoogleSignInInProgress = true,
                hasError = false,
            )
        }

        modelScope.launch {
            val result = loginWithGoogleUseCase()

            dataState.update { state ->
                when (result) {
                    LoginResult.Success,
                    LoginResult.Cancelled,
                    -> state.copy(
                        isGoogleSignInInProgress = false,
                        hasError = false,
                    )

                    LoginResult.Error -> state.copy(
                        isGoogleSignInInProgress = false,
                        hasError = true,
                    )
                }
            }
        }
    }

    data class DataState(
        val isGoogleSignInInProgress: Boolean = false,
        val hasError: Boolean = false,
    )

    class Factory(
        private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
        private val coroutineDispatchers: CoroutineDispatchers,
    ) {
        operator fun invoke() = LoginModel(
            loginWithGoogleUseCase = loginWithGoogleUseCase,
            coroutineDispatchers = coroutineDispatchers,
        )
    }
}
