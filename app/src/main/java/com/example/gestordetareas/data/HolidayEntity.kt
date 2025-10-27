package com.example.gestordetareas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un día feriado en la base de datos
 * Contiene información sobre los días feriados de Chile
 */
@Entity(tableName = "holidays")
data class HolidayEntity(
    @PrimaryKey
    val id: String, // Formato: "YYYY-MM-DD"
    val name: String, // Nombre del feriado
    val date: String, // Fecha en formato "YYYY-MM-DD"
    val type: String, // Tipo: "Nacional", "Religioso", "Regional"
    val isRecurring: Boolean = true, // Si se repite cada año
    val year: Int // Año del feriado
)
