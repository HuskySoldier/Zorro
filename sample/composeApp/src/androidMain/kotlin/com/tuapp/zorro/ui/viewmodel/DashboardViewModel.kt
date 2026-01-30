package com.tuapp.zorro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuapp.zorro.domain.repository.ACZRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(repository: ACZRepository) : ViewModel() {

    // Combinamos los flujos de Ramos y Tareas para un solo estado de UI
    val uiState: StateFlow<DashboardUiState> = combine(
        repository.obtenerTodosLosRamos(),
        repository.obtenerTareasPriorizadas()
    ) { ramos, tareas ->
        DashboardUiState(
            ramos = ramos,
            tareasPrioritarias = tareas,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )
}