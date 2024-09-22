package com.braiso22.turnos.tasks.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.tasks.domain.Task
import com.braiso22.turnos.tasks.domain.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data object NavigateBack : UiEvent()
        data object ShowError : UiEvent()
    }

    fun onEvent(event: AddTaskEvent) {
        when (event) {
            is AddTaskEvent.OnChangedName -> {
                _name.update {
                    event.name
                }
            }

            is AddTaskEvent.OnClickSave -> {
                viewModelScope.launch {
                    tasksRepository.saveTask(Task(id = "", name = name.value)).collect { result ->
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

            is AddTaskEvent.OnClickCancel -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.NavigateBack)
                }
            }
        }
    }
}