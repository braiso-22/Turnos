package com.braiso22.turnos.users.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.users.domain.SyncUsers
import com.braiso22.turnos.users.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val syncUsers: SyncUsers,
) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data object OnBadLogin : UiEvent()
        data object OnBadRegister : UiEvent()
        data object NoInternet : UiEvent()
        data object OnLogin : UiEvent()
        data object OnRegister : UiEvent()
    }

    init {
        viewModelScope.launch {
            syncUsers()
        }
    }

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.OnEmailChange -> {
                _email.update { event.email }
            }

            LoginScreenEvent.OnLogin -> {
                viewModelScope.launch {
                    repository.emailLogin(_email.value, _password.value).collect { resource ->
                        when (resource) {
                            is Resource.Error -> {
                                _isLoading.update { false }
                                _eventFlow.emit(UiEvent.OnBadLogin)
                            }

                            is Resource.Loading -> {
                                _isLoading.update { true }
                            }

                            is Resource.Success -> {
                                _isLoading.update { false }
                                _eventFlow.emit(UiEvent.OnLogin)
                            }
                        }
                    }
                }
            }

            is LoginScreenEvent.OnPasswordChange -> {
                _password.update { event.password }
            }

            LoginScreenEvent.OnRegister -> {
                viewModelScope.launch {
                    repository.emailSignIn(email = email.value, password = password.value).collect {
                        when (it) {
                            is Resource.Error -> {
                                _isLoading.update { false }
                                _eventFlow.emit(UiEvent.OnBadRegister)
                            }

                            is Resource.Loading -> {
                                _isLoading.update { true }
                            }

                            is Resource.Success -> {
                                _isLoading.update { false }
                                _eventFlow.emit(UiEvent.OnRegister)
                            }
                        }
                    }
                }
            }
        }
    }
}
