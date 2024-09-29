package com.braiso22.turnos.tasks.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braiso22.turnos.tasks.domain.GetTasks
import com.braiso22.turnos.tasks.domain.SyncTasks
import com.braiso22.turnos.tasks.presentation.list.components.TaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val syncTasks: SyncTasks,
    private val getTasks: GetTasks,
) : ViewModel() {
    private var realTasks = MutableStateFlow<List<TaskUiState>>(emptyList())

    private val _tasks = MutableStateFlow<List<TaskUiState>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class NavigateToTask(val id: String) : UiEvent()
        data class NavigateToEditTask(val id: String) : UiEvent()
        data object NavigateToAddTask : UiEvent()
        data object NavigateToReceipts : UiEvent()
    }

    init {
        viewModelScope.launch {
            syncTasks()
        }
        viewModelScope.launch {
            getTasks().collect { tasks ->
                realTasks.update {
                    tasks.map { task ->
                        TaskUiState(
                            id = task.id,
                            name = task.name,
                        )
                    }
                }
                _tasks.update {
                    getFilteredTasks()
                }
            }
        }
    }

    fun onEvent(event: TasksScreenEvent) {
        when (event) {
            is TasksScreenEvent.OnChangedSearch -> {
                _searchText.update {
                    event.search
                }

                _tasks.update {
                    getFilteredTasks()
                }
            }

            is TasksScreenEvent.OnClickEdit -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.NavigateToEditTask(event.id))
                }
            }

            TasksScreenEvent.OnClickNew -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.NavigateToAddTask)
                }
            }

            is TasksScreenEvent.OnClickTask -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.NavigateToTask(event.id))
                }
            }

            TasksScreenEvent.OnClickReceipts -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.NavigateToReceipts)
                }
            }
        }
    }

    private fun getFilteredTasks(): List<TaskUiState> {
        val cleanFilter = _searchText.value.lowercase(Locale.ROOT)
        return realTasks.value.filter { task ->
            task.name.lowercase(Locale.ROOT).contains(cleanFilter)
        }
    }
}