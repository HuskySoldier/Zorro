package com.tuapp.zorro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuapp.zorro.domain.model.Ramo
import com.tuapp.zorro.domain.repository.ACZRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddSubjectViewModel(private val repository: ACZRepository) : ViewModel() {

    // Estado de la UI: Nombre del ramo y color seleccionado
    private val _nombreRamo = MutableStateFlow("")
    val nombreRamo: StateFlow<String> = _nombreRamo

    private val _colorSeleccionado = MutableStateFlow("#3F51B5") // Azul por defecto
    val colorSeleccionado: StateFlow<String> = _colorSeleccionado

    fun onNombreChange(nuevoNombre: String) {
        _nombreRamo.value = nuevoNombre
    }

    fun onColorChange(nuevoColor: String) {
        _colorSeleccionado.value = nuevoColor
    }

    fun guardarRamo() {
        val nombre = _nombreRamo.value.trim()
        if (nombre.isNotEmpty()) {
            viewModelScope.launch {
                repository.guardarRamo(
                    Ramo(nombre = nombre, colorHex = _colorSeleccionado.value)
                )
                // Limpiar campos tras guardar
                _nombreRamo.value = ""
            }
        }
    }
}