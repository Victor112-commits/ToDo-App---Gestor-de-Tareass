package com.example.gestordetareas.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Clase entidad que representa una tarea en la base de datos Room
 * Contiene todos los campos necesarios para la gestión de tareas
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val priority: String = "Media", // Alta, Media, Baja
    val category: String = "General", // General, Trabajo, Personal, Salud, etc.
    val isCompleted: Boolean = false,
    val dateCreated: Long = System.currentTimeMillis(),
    val dueDate: Long? = null // Optional due date
)
