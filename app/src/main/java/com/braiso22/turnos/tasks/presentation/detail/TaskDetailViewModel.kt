package com.braiso22.turnos.tasks.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.executions.domain.Execution
import com.braiso22.turnos.executions.domain.ExecutionsRepository
import com.braiso22.turnos.executions.presentation.same_type.ExecutionUiState
import com.braiso22.turnos.users.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val executionsRepository: ExecutionsRepository,
    private val usersRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<List<ExecutionUiState>>(emptyList())
    val executions = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data object NavigateBack : UiEvent()
        data object ShowError : UiEvent()
    }

    fun onInit(id: String) {
        viewModelScope.launch {

            executionsRepository.getExecutionsByTaskIds(id).collect { executions ->
                _state.update {
                    executions.map {
                        val userName = usersRepository.getUserById(it.userId)?.userName ?: "user"
                        ExecutionUiState(
                            id = it.id,
                            imageUrl = null,
                            userName = userName,
                            date = it.dateTime.toLocalDate().toString(),
                            time = it.dateTime.toLocalTime().toString(),
                            isConfirmed = it.isConfirmed
                        )
                    }
                }
            }
        }
    }

    fun onClickNew(id: String) {
        viewModelScope.launch {
            executionsRepository.saveExecution(
                Execution(
                    id = "",
                    dateTime = LocalDateTime.now(),
                    isConfirmed = false,
                    taskId = id,
                    userId = "test"
                )
            ).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _isLoading.update {
                            false
                        }
                        _eventFlow.emit(UiEvent.ShowError)
                    }

                    is Resource.Loading -> {
                        _isLoading.update {
                            true
                        }
                    }

                    is Resource.Success -> {
                        _isLoading.update {
                            false
                        }
                        _eventFlow.emit(UiEvent.NavigateBack)
                    }
                }
            }
        }
    }
}