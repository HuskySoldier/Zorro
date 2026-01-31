package com.example.acz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.acz.AczApplication
import com.example.acz.data.local.entity.NotaEntity
import com.example.acz.data.local.entity.RamoEntity
import com.example.acz.data.local.entity.SemestreEntity
import com.example.acz.data.local.entity.TareaEntity
import com.example.acz.data.repositories.AppRepository // Asegúrate que coincida con tu paquete de repositorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: AppRepository) : ViewModel() {

    // ----------------------------------------------------------
    // 1. ESTADOS (Listas observables)
    // ----------------------------------------------------------

    // Lista de Tareas Pendientes (Pestaña 1)
    val tareasPendientes: StateFlow<List<TareaEntity>> = repository.tareasPendientes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Lista de Tareas Completadas (Pestaña 2 - Historial)
    val tareasCompletadas: StateFlow<List<TareaEntity>> = repository.tareasCompletadas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Lista de Ramos (Para los desplegables)
    val ramosDisponibles: StateFlow<List<RamoEntity>> = repository.todosLosRamos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //cambios

    fun obtenerNotas(ramoId: Long): Flow<List<NotaEntity>> {
        return repository.obtenerNotasDeRamo(ramoId)
    }

    fun actualizarNota(nota: NotaEntity) {
        viewModelScope.launch {
            repository.actualizarNota(nota)
        }
    }

    fun guardarNota(nombre: String, valor: Double, porcentaje: Int, ramoId: Long) {
        viewModelScope.launch {
            val nuevaNota = NotaEntity(nombre = nombre, valor = valor, porcentaje = porcentaje, ramoId = ramoId)
            repository.insertarNota(nuevaNota)
        }
    }

    fun borrarNota(nota: NotaEntity) {
        viewModelScope.launch {
            repository.borrarNota(nota)
        }
    }
    // --- HORARIO ---
    val horarioCompleto = repository.todoElHorario
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun guardarBloque(dia: Int, inicio: String, fin: String, sala: String, ramoId: Long) {
        viewModelScope.launch {
            repository.insertarBloque(
                com.example.acz.data.local.entity.HorarioEntity(
                    diaSemana = dia,
                    horaInicio = inicio,
                    horaFin = fin,
                    sala = sala.ifBlank { null },
                    ramoId = ramoId
                )
            )
        }
    }

    fun borrarBloque(bloque: com.example.acz.data.local.entity.HorarioEntity) {
        viewModelScope.launch { repository.borrarBloque(bloque) }
    }
    // ----------------------------------------------------------
    // 2. ACCIONES DE TAREAS
    // ----------------------------------------------------------

    fun actualizarTarea(id: Long, titulo: String, tipo: String, peso: Int, fecha: Long, ramoId: Long, estadoActual: String) {
        viewModelScope.launch {
            val tareaEditada = TareaEntity(
                id = id, // ¡IMPORTANTE! Mantenemos el mismo ID para sobrescribir
                titulo = titulo,
                tipo = tipo,
                peso = peso,
                fechaEntrega = fecha,
                ramoId = ramoId,
                estado = estadoActual
            )
            repository.actualizarTarea(tareaEditada)
        }
    }

    // Guarda una nueva tarea desde el formulario
    fun guardarNuevaTarea(titulo: String, tipo: String, peso: Int, fecha: Long, ramoId: Long) {
        viewModelScope.launch {
            val nuevaTarea = TareaEntity(
                titulo = titulo,
                tipo = tipo,
                peso = peso,
                fechaEntrega = fecha,
                ramoId = ramoId,
                estado = "PENDIENTE"
            )
            repository.insertarTarea(nuevaTarea)
        }
    }

    // Marca una tarea como lista (Mueve a Historial)
    fun completarTarea(tarea: TareaEntity) {
        viewModelScope.launch {
            // Creamos una copia con el estado cambiado
            val tareaActualizada = tarea.copy(estado = "COMPLETADA")
            repository.actualizarTarea(tareaActualizada)
        }
    }

    // Reactiva una tarea del historial (Mueve a Pendientes)
    fun reactivarTarea(tarea: TareaEntity) {
        viewModelScope.launch {
            val tareaActualizada = tarea.copy(estado = "PENDIENTE")
            repository.actualizarTarea(tareaActualizada)
        }
    }

    // Elimina la tarea definitivamente de la base de datos
    fun borrarTareaDefinitiva(tarea: TareaEntity) {
        viewModelScope.launch {
            repository.borrarTarea(tarea)
        }
    }


    // ----------------------------------------------------------
    // 3. LOGICA DE RAMOS Y SEMESTRES
    // ----------------------------------------------------------

    fun guardarNuevoRamo(nombre: String, colorHex: String, profesor: String, email: String) {
        viewModelScope.launch {
            // Buscamos el ID del semestre actual (o creamos uno si no existe)
            val semestres = repository.todosLosSemestres.firstOrNull()

            val semestreId = if (semestres.isNullOrEmpty()) {
                val nuevo = SemestreEntity(nombre = "2026-1", esActual = true)
                repository.insertarSemestre(nuevo)
            } else {
                semestres.first().id
            }

            val nuevoRamo = RamoEntity(
                nombre = nombre,
                colorHex = colorHex,
                semestreId = semestreId,
                profesor = profesor.ifBlank { null }, // Si está vacío, guardamos null
                emailProfesor = email.ifBlank { null }
            )
            repository.insertarRamo(nuevoRamo)
        }
    }

    fun borrarRamo(ramo: RamoEntity) {
        viewModelScope.launch {
            repository.borrarRamo(ramo)
        }
    }

    // ----------------------------------------------------------
    // 4. FACTORY
    // ----------------------------------------------------------
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AczApplication)
                HomeViewModel(application.repository)
            }
        }
    }
}