package com.tuapp.zorro.data.repository

import com.tuapp.zorro.data.local.AppDao
import com.tuapp.zorro.data.local.RamoEntity
import com.tuapp.zorro.data.local.TareaEntity
import com.tuapp.zorro.domain.model.Ramo
import com.tuapp.zorro.domain.model.Tarea
import com.tuapp.zorro.domain.model.TipoTarea
import com.tuapp.zorro.domain.repository.ACZRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class ACZRepositoryImpl(private val dao: AppDao) : ACZRepository {

    override suspend fun guardarRamo(ramo: Ramo) {
        dao.insertarRamo(RamoEntity(nombre = ramo.nombre, colorHex = ramo.colorHex))
    }

    override fun obtenerTodosLosRamos(): Flow<List<Ramo>> =
        dao.obtenerTodosLosRamos().map { entities ->
            entities.map { Ramo(it.id, it.nombre, it.colorHex) }
        }

    override suspend fun guardarTarea(tarea: Tarea) {
        dao.insertarTarea(TareaEntity(
            titulo = tarea.titulo,
            fechaEntrega = tarea.fechaEntrega,
            peso = tarea.peso,
            ramoId = tarea.ramoId,
            tipo = tarea.tipo.name
        ))
    }

    override fun obtenerTareasPriorizadas(): Flow<List<Tarea>> =
        dao.obtenerTodasLasTareas().map { entities ->
            entities.map { entity ->
                Tarea(
                    id = entity.id,
                    titulo = entity.titulo,
                    fechaEntrega = entity.fechaEntrega,
                    peso = entity.peso,
                    ramoId = entity.ramoId,
                    tipo = TipoTarea.valueOf(entity.tipo)
                )
            }.sortedByDescending { calcularPrioridad(it) }
        }

    /**
     * Algoritmo de Prioridad ACZ:
     * Calcula la urgencia estratégica basándose en el peso de la evaluación
     * y la finitud del tiempo disponible.
     */
    private fun calcularPrioridad(tarea: Tarea): Double {
        val ahora = Clock.System.now().toEpochMilliseconds()
        val unDiaEnMs = 86400000L
        val diferenciaMs = tarea.fechaEntrega - ahora
        val diasRestantes = (diferenciaMs / unDiaEnMs).coerceAtLeast(0)

        // P = peso * (1 / (dias + 1))
        return tarea.peso * (1.0 / (diasRestantes + 1))
    }
}