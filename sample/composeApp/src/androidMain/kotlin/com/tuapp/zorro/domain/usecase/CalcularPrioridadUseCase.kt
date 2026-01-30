package com.tuapp.zorro.domain.usecase

import com.tuapp.zorro.domain.model.Tarea
import kotlinx.datetime.Clock

class CalcularPrioridadUseCase {
    fun ejecutar(tarea: Tarea): Double {
        val ahora = Clock.System.now().toEpochMilliseconds()
        val unDiaEnMs = 86400000L
        val diferenciaMs = tarea.fechaEntrega - ahora
        val diasRestantes = (diferenciaMs / unDiaEnMs).coerceAtLeast(0)

        return tarea.peso * (1.0 / (diasRestantes + 1))
    }
}