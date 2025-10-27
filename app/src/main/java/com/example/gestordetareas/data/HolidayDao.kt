package com.example.gestordetareas.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Interfaz de acceso a datos (DAO) para HolidayEntity
 * Define todas las operaciones de base de datos para días feriados
 */
@Dao
interface HolidayDao {
    
    /**
     * Insertar un nuevo día feriado en la base de datos
     * @param holiday El día feriado a insertar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoliday(holiday: HolidayEntity)
    
    /**
     * Insertar múltiples días feriados
     * @param holidays Lista de días feriados a insertar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolidays(holidays: List<HolidayEntity>)
    
    /**
     * Obtener todos los días feriados
     * @return LiveData con lista de todos los días feriados
     */
    @Query("SELECT * FROM holidays ORDER BY date ASC")
    fun getAllHolidays(): LiveData<List<HolidayEntity>>
    
    /**
     * Obtener días feriados de un año específico
     * @param year El año a consultar
     * @return LiveData con lista de días feriados del año
     */
    @Query("SELECT * FROM holidays WHERE year = :year ORDER BY date ASC")
    fun getHolidaysByYear(year: Int): LiveData<List<HolidayEntity>>
    
    /**
     * Obtener días feriados de un mes específico
     * @param year El año
     * @param month El mes (1-12)
     * @return LiveData con lista de días feriados del mes
     */
    @Query("SELECT * FROM holidays WHERE year = :year AND CAST(substr(date, 6, 2) AS INTEGER) = :month ORDER BY date ASC")
    fun getHolidaysByMonth(year: Int, month: Int): LiveData<List<HolidayEntity>>
    
    /**
     * Verificar si una fecha específica es feriado
     * @param date Fecha en formato "YYYY-MM-DD"
     * @return El día feriado si existe, null si no
     */
    @Query("SELECT * FROM holidays WHERE date = :date LIMIT 1")
    suspend fun getHolidayByDate(date: String): HolidayEntity?
    
    /**
     * Obtener días feriados de un rango de fechas
     * @param startDate Fecha de inicio en formato "YYYY-MM-DD"
     * @param endDate Fecha de fin en formato "YYYY-MM-DD"
     * @return LiveData con lista de días feriados en el rango
     */
    @Query("SELECT * FROM holidays WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getHolidaysInRange(startDate: String, endDate: String): LiveData<List<HolidayEntity>>
    
    /**
     * Eliminar todos los días feriados
     */
    @Query("DELETE FROM holidays")
    suspend fun deleteAllHolidays()
    
    /**
     * Eliminar días feriados de un año específico
     * @param year El año a eliminar
     */
    @Query("DELETE FROM holidays WHERE year = :year")
    suspend fun deleteHolidaysByYear(year: Int)
}
