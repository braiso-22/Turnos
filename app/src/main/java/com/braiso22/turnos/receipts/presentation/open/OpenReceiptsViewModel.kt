package com.braiso22.turnos.receipts.presentation.open

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braiso22.turnos.receipts.presentation.open.components.ReceiptUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenReceiptsViewModel @Inject constructor() : ViewModel() {
    private val _receipts = MutableStateFlow<Map<String, List<ReceiptUiState>>>(emptyMap())
    val receipts = _receipts.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data object NavigateToHistory : UiEvent()
        data object NavigateToTasks : UiEvent()
    }

    fun onEvent(event: OpenReceiptEvent) {
        when (event) {
            is OpenReceiptEvent.OnClickConfirm -> {
                viewModelScope.launch {
                    TODO()
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
