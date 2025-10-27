package com.example.gestordetareas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa una tarea eliminada en la papelera de reciclaje
 * Almacena tareas eliminadas para permitir su restauración
 */
@Entity(tableName = "deleted_tasks")
data class DeletedTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalTaskId: Long, // ID original de la tarea
    val title: String,
    val description: String? = null,
    val priority: String = "Media",
    val category: String = "General",
    val isCompleted: Boolean = false,
    val dateCreated: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val deletedAt: Long = System.currentTimeMillis() // Fecha de eliminación
)
