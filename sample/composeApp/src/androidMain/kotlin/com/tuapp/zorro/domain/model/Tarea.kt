package com.tuapp.zorro.domain.model

import kotlinx.datetime.Instant

enum class TipoTarea { PRUEBA, TRABAJO, EXPOSICION }

data class Tarea(
    val id: Long = 0,
    val titulo: String,
    val fechaEntrega: Long, // Guardaremos milisegundos
    val peso: Double,
    val ramoId: Long,
    val tipo: TipoTarea
)