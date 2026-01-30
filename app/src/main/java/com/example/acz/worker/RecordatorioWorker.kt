package com.example.acz.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.acz.R
import com.example.acz.data.local.AppDatabase
import java.util.concurrent.TimeUnit

class RecordatorioWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 1. Obtener la base de datos
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.tareaDao()

        // 2. Definir rango de tiempo (Próximas 24 horas)
        val hoy = System.currentTimeMillis()
        val manana = hoy + TimeUnit.DAYS.toMillis(1)

        // 3. Buscar tareas
        val tareasUrgentes = dao.getTareasProximas(hoy, manana)

        // 4. Si hay tareas, lanzar notificación
        if (tareasUrgentes.isNotEmpty()) {
            val cantidad = tareasUrgentes.size
            val primeraTarea = tareasUrgentes.first().titulo

            val mensaje = if (cantidad == 1) {
                "¡Ojo! Mañana entregas: $primeraTarea"
            } else {
                "¡Atento! Tienes $cantidad entregas próximas ($primeraTarea y más...)"
            }

            mostrarNotificacion(mensaje)
        }

        return Result.success()
    }

    private fun mostrarNotificacion(mensaje: String) {
        val context = applicationContext
        val channelId = "canal_academico"
        val notificationId = 1

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal (Obligatorio en Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios Académicos",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, channelId)
            // Usamos un icono de sistema por defecto para no complicarnos con recursos ahora
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Recordatorio de Estudio")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}