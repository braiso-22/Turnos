package com.braiso22.turnos.executions.presentation.task_executions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.executions.domain.Execution
import com.braiso22.turnos.executions.domain.use_cases.GetExecutionsByTaskId
import com.braiso22.turnos.executions.domain.use_cases.SaveExecution
import com.braiso22.turnos.executions.domain.use_cases.SyncExecutions
import com.braiso22.turnos.executions.presentation.task_executions.components.ExecutionUiState
import com.braiso22.turnos.users.domain.SyncUsers
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
class TaskExecutionsViewModel @Inject constructor(
    private val getExecutionsByTaskId: GetExecutionsByTaskId,
    private val saveExecution: SaveExecution,
    private val syncUsers: SyncUsers,
    private val syncExecutions: SyncExecutions,
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
            syncUsers()
        }
        viewModelScope.launch {
            syncExecutions()
        }
        viewModelScope.launch {
            getExecutionsByTaskId(id).collect { executions ->
                _state.update {
                    executions.map {
                        ExecutionUiState(
                            id = it.execution.id,
                            imageUrl = null,
                            userName = it.user?.username ?: "",
                            date = it.execution.dateTime.toLocalDate().toString(),
                            time = it.execution.dateTime.toLocalTime().toString(),
                            isConfirmed = it.execution.isConfirmed
                        )
                    }
                }
            }
        }
    }

    fun onClickNew(id: String) {
        viewModelScope.launch {
            saveExecution(
                Execution(
                    dateTime = LocalDateTime.now(),
                    isConfirmed = false,
                    taskId = id,
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
                    }
                }
            }
        }
    }

    fun clickBack(){
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.NavigateBack)
        }
    }
}