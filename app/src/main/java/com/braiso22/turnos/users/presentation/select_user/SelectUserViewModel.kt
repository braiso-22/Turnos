package com.braiso22.turnos.users.presentation.select_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.users.domain.CreateUser
import com.braiso22.turnos.users.domain.SyncUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectUserViewModel @Inject constructor(
    private val createUser: CreateUser,
    private val syncUsers: SyncUsers,
) : ViewModel() {
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data object OnRegister : UiEvent()
        data object OnBadRegister : UiEvent()
    }

    init {
        viewModelScope.launch {
            syncUsers()
        }
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onSelect() {
        viewModelScope.launch {
            createUser(username.value).collect { resource ->
                when (resource) {
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

    fun onChangeUsername(username: String) {
        _username.update {
            username
        }
    }
}
