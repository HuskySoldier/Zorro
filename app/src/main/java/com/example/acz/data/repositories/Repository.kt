package com.example.acz.data.repositories

import com.example.acz.data.local.dao.HorarioDao // <--- Asegúrate de tener este import
import com.example.acz.data.local.dao.NotaDao
import com.example.acz.data.local.dao.RamoDao
import com.example.acz.data.local.dao.SemestreDao
import com.example.acz.data.local.dao.TareaDao
import com.example.acz.data.local.entity.HorarioEntity
import com.example.acz.data.local.entity.NotaEntity
import com.example.acz.data.local.entity.RamoEntity
import com.example.acz.data.local.entity.SemestreEntity
import com.example.acz.data.local.entity.TareaEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(
    val semestreDao: SemestreDao,
    private val ramoDao: RamoDao,
    private val tareaDao: TareaDao,
    private val notaDao: NotaDao,
    private val horarioDao: HorarioDao // <--- 1. AGREGAMOS ESTO AQUÍ (Faltaba)
) {

    // --- SEMESTRES ---
    val todosLosSemestres: Flow<List<SemestreEntity>> = semestreDao.getAllSemestres()

    suspend fun insertarSemestre(semestre: SemestreEntity): Long {
        return semestreDao.insertSemestre(semestre)
    }

    suspend fun borrarSemestre(semestre: SemestreEntity) {
        semestreDao.deleteSemestre(semestre.id)
    }

    // --- RAMOS ---
    val todosLosRamos: Flow<List<RamoEntity>> = ramoDao.getAllRamos()

    fun getRamosPorSemestre(semestreId: Long): Flow<List<RamoEntity>> {
        return ramoDao.getRamosBySemestre(semestreId)
    }

    suspend fun insertarRamo(ramo: RamoEntity): Long {
        return ramoDao.insertRamo(ramo)
    }

    suspend fun borrarRamo(ramo: RamoEntity) {
        ramoDao.deleteRamo(ramo.id)
    }

    // --- TAREAS ---
    val tareasCompletadas: Flow<List<TareaEntity>> = tareaDao.getTareasCompletadas()
    val tareasPendientes: Flow<List<TareaEntity>> = tareaDao.getTareasPendientes()

    fun getTareasPorRamo(ramoId: Long): Flow<List<TareaEntity>> {
        return tareaDao.getTareasByRamo(ramoId)
    }

    suspend fun insertarTarea(tarea: TareaEntity) {
        tareaDao.insertTarea(tarea)
    }

    suspend fun actualizarTarea(tarea: TareaEntity) {
        tareaDao.updateTarea(tarea)
    }

    suspend fun borrarTarea(tarea: TareaEntity) {
        tareaDao.deleteTarea(tarea.id)
    }

    // --- NOTAS ---
    fun obtenerNotasDeRamo(ramoId: Long): Flow<List<NotaEntity>> = notaDao.getNotasPorRamo(ramoId)

    suspend fun insertarNota(nota: NotaEntity) = notaDao.insertNota(nota)

    suspend fun borrarNota(nota: NotaEntity) = notaDao.deleteNota(nota)

    suspend fun actualizarNota(nota: NotaEntity) = notaDao.updateNota(nota)

    // --- HORARIO ---
    // Aquí usamos la instancia 'horarioDao' (minúscula), no la clase 'HorarioDao' (mayúscula)
    val todoElHorario: Flow<List<HorarioEntity>> = horarioDao.getAllHorarios()

    suspend fun insertarBloque(h: HorarioEntity) = horarioDao.insertHorario(h)

    suspend fun borrarBloque(h: HorarioEntity) = horarioDao.deleteHorario(h)
}