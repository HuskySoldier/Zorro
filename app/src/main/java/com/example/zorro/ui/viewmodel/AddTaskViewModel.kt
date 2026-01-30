package com.tuapp.zorro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuapp.zorro.domain.model.Tarea
import com.tuapp.zorro.domain.model.TipoTarea
import com.tuapp.zorro.domain.repository.ACZRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.todayIn

class AddTaskViewModel(private val repository: ACZRepository) : ViewModel() {

    private val _titulo = MutableStateFlow("")
    val titulo: StateFlow<String> = _titulo

    private val _peso = MutableStateFlow("10.0") // Valor por defecto
    val peso: StateFlow<String> = _peso

    // Por defecto, vence mañana para evitar divisiones por cero en el algoritmo
    private val _fechaEntrega = MutableStateFlow(
        Clock.System.todayIn(TimeZone.currentSystemDefault())
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    )

    fun onTituloChange(nuevo: String) { _titulo.value = nuevo }
    fun onPesoChange(nuevo: String) { _peso.value = nuevo }

    fun guardarTarea(ramoId: Long) {
        val pesoDouble = _peso.value.toDoubleOrNull() ?: 0.0
        if (_titulo.value.isNotEmpty() && ramoId != 0L) {
            viewModelScope.launch {
                repository.guardarTarea(
                    Tarea(
                        titulo = _titulo.value,
                        fechaEntrega = _fechaEntrega.value,
                        peso = pesoDouble,
                        ramoId = ramoId,
                        tipo = TipoTarea.PRUEBA // Simplificado para MVP
                    )
                )
                _titulo.value = "" // Limpiar tras guardar
            }
        }
    }
}