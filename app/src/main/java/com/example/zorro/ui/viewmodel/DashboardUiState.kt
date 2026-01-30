package com.tuapp.zorro.ui.viewmodel

import com.tuapp.zorro.domain.model.Ramo
import com.tuapp.zorro.domain.model.Tarea

data class DashboardUiState(
    val ramos: List<Ramo> = emptyList(),
    val tareasPrioritarias: List<Tarea> = emptyList(),
    val isLoading: Boolean = true
)