package com.braiso22.turnos.executions.presentation.open_executions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.executions.domain.use_cases.ConfirmExecution
import com.braiso22.turnos.executions.domain.use_cases.GetOpenExecutions
import com.braiso22.turnos.executions.domain.use_cases.SyncExecutions
import com.braiso22.turnos.executions.presentation.open_executions.components.ReceiptUiState
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
class OpenReceiptsViewModel @Inject constructor(
    private val getOpenExecutionsByUserId: GetOpenExecutions,
    private val confirmExecution: ConfirmExecution,
    private val syncExecutions: SyncExecutions,
    private val syncUsers: SyncUsers,
) : ViewModel() {
    private val _receipts = MutableStateFlow<Map<String, List<ReceiptUiState>>>(emptyMap())
    val receipts = _receipts.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data object NavigateToHistory : UiEvent()
        data object NavigateToTasks : UiEvent()
    }

    fun onInit() {
        viewModelScope.launch {
            syncUsers()
        }
        viewModelScope.launch {
            syncExecutions()
        }
        viewModelScope.launch {
            getOpenExecutionsByUserId().collect { executions ->
                _receipts.update { _ ->
                    executions.groupBy {
                        it.execution.dateTime.toLocalDate().toString()
                    }.mapValues { (_, executionsWithData) ->
                        executionsWithData.map { executionWithData ->
                            val execution = executionWithData.execution
                            ReceiptUiState(
                                id = execution.id,
                                imageUrl = executionWithData.user?.imageUrl,
                                taskName = executionWithData.task?.name ?: "",
                                userName = executionWithData.user?.username ?: "",
                                time = execution.dateTime.toLocalTime().toString(),
                            )
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: OpenReceiptEvent) {
        when (event) {
            is OpenReceiptEvent.OnClickConfirm -> {
                viewModelScope.launch {
                    confirmExecution(event.id).collect { resource ->
                        when (resource) {
                            is Resource.Error -> {

                            }

                            is Resource.Loading -> {

                            }

                            is Resource.Success -> {

                            }
                        }
                    }
                }
            }

            OpenReceiptEvent.OnClickHistory -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.NavigateToHistory)
                }
            }

            OpenReceiptEvent.OnClickTasks -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.NavigateToTasks)
                }
            }
        }
    }
}
