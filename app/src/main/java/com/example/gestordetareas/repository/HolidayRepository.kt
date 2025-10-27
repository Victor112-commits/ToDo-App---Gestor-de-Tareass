package com.example.gestordetareas.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.gestordetareas.data.HolidayDao
import com.example.gestordetareas.data.TaskDatabase
import com.example.gestordetareas.data.HolidayEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Repositorio para manejar los días feriados de Chile
 * Proporciona una API limpia para el acceso a datos de días feriados
 */
class HolidayRepository(application: Application) {
    
    private val holidayDao: HolidayDao = TaskDatabase.getDatabase(application).holidayDao()
    
    /**
     * Obtener todos los días feriados
     * @return LiveData con lista de todos los días feriados
     */
    fun getAllHolidays(): LiveData<List<HolidayEntity>> = holidayDao.getAllHolidays()
    
    /**
     * Obtener días feriados de un año específico
     * @param year El año a consultar
     * @return LiveData con lista de días feriados del año
     */
    fun getHolidaysByYear(year: Int): LiveData<List<HolidayEntity>> = 
        holidayDao.getHolidaysByYear(year)
    
    /**
     * Obtener días feriados de un mes específico
     * @param year El año
     * @param month El mes (1-12)
     * @return LiveData con lista de días feriados del mes
     */
    fun getHolidaysByMonth(year: Int, month: Int): LiveData<List<HolidayEntity>> = 
        holidayDao.getHolidaysByMonth(year, month)
    
    /**
     * Verificar si una fecha específica es feriado
     * @param date Fecha en formato "YYYY-MM-DD"
     * @return El día feriado si existe, null si no
     */
    suspend fun getHolidayByDate(date: String): HolidayEntity? = withContext(Dispatchers.IO) {
        holidayDao.getHolidayByDate(date)
    }
    
    /**
     * Obtener días feriados de un rango de fechas
     * @param startDate Fecha de inicio en formato "YYYY-MM-DD"
     * @param endDate Fecha de fin en formato "YYYY-MM-DD"
     * @return LiveData con lista de días feriados en el rango
     */
    fun getHolidaysInRange(startDate: String, endDate: String): LiveData<List<HolidayEntity>> = 
        holidayDao.getHolidaysInRange(startDate, endDate)
    
    /**
     * Inicializar los días feriados de Chile para un año específico
     * @param year El año a inicializar
     */
    suspend fun initializeHolidaysForYear(year: Int) = withContext(Dispatchers.IO) {
        val holidays = getChileanHolidaysForYear(year)
        holidayDao.insertHolidays(holidays)
    }
    
    /**
     * Obtener los días feriados de Chile para un año específico
     * @param year El año
     * @return Lista de días feriados de Chile para el año
     */
    private fun getChileanHolidaysForYear(year: Int): List<HolidayEntity> {
        val holidays = mutableListOf<HolidayEntity>()
        
        // Días feriados fijos de Chile
        holidays.add(HolidayEntity(
            id = "$year-01-01",
            name = "Año Nuevo",
            date = "$year-01-01",
            type = "Nacional",
            year = year
        ))
        
        holidays.add(HolidayEntity(
            id = "$year-05-01",
            name = "Día del Trabajador",
            date = "$year-05-01",
            type = "Nacional",
            year = year
        ))
        
        holidays.add(HolidayEntity(
            id = "$year-05-21",
            name = "Día de las Glorias Navales",
            date = "$year-05-21",
            type = "Nacional",
            year = year
        ))
        
        holidays.add(HolidayEntity(
            id = "$year-09-18",
            name = "Fiestas Patrias",
            date = "$year-09-18",
            type = "Nacional",
            year = year
        ))
        
        holidays.add(HolidayEntity(
            id = "$year-09-19",
            name = "Glorias del Ejército",
            date = "$year-09-19",
            type = "Nacional",
            year = year
        ))
        
        holidays.add(HolidayEntity(
            id = "$year-12-25",
            name = "Navidad",
            date = "$year-12-25",
            type = "Religioso",
            year = year
        ))
        
        // Días feriados variables (aproximados)
        // Semana Santa - Viernes Santo (aproximado)
        val easterDate = calculateEaster(year)
        val goodFriday = Calendar.getInstance().apply { 
            timeInMillis = easterDate.timeInMillis - (2 * 24 * 60 * 60 * 1000L)
        }
        val easterMonday = Calendar.getInstance().apply { 
            timeInMillis = easterDate.timeInMillis + (1 * 24 * 60 * 60 * 1000L)
        }
        
        holidays.add(HolidayEntity(
            id = "$year-${String.format("%02d", goodFriday.get(Calendar.MONTH) + 1)}-${String.format("%02d", goodFriday.get(Calendar.DAY_OF_MONTH))}",
            name = "Viernes Santo",
            date = "$year-${String.format("%02d", goodFriday.get(Calendar.MONTH) + 1)}-${String.format("%02d", goodFriday.get(Calendar.DAY_OF_MONTH))}",
            type = "Religioso",
            year = year
        ))
        
        holidays.add(HolidayEntity(
            id = "$year-${String.format("%02d", easterMonday.get(Calendar.MONTH) + 1)}-${String.format("%02d", easterMonday.get(Calendar.DAY_OF_MONTH))}",
            name = "Lunes de Pascua",
            date = "$year-${String.format("%02d", easterMonday.get(Calendar.MONTH) + 1)}-${String.format("%02d", easterMonday.get(Calendar.DAY_OF_MONTH))}",
            type = "Religioso",
            year = year
        ))
        
        // Día de la Virgen del Carmen (16 de julio)
        holidays.add(HolidayEntity(
            id = "$year-07-16",
            name = "Día de la Virgen del Carmen",
            date = "$year-07-16",
            type = "Religioso",
            year = year
        ))
        
        // Día de la Asunción (15 de agosto)
        holidays.add(HolidayEntity(
            id = "$year-08-15",
            name = "Día de la Asunción",
            date = "$year-08-15",
            type = "Religioso",
            year = year
        ))
        
        // Día de Todos los Santos (1 de noviembre)
        holidays.add(HolidayEntity(
            id = "$year-11-01",
            name = "Día de Todos los Santos",
            date = "$year-11-01",
            type = "Religioso",
            year = year
        ))
        
        // Inmaculada Concepción (8 de diciembre)
        holidays.add(HolidayEntity(
            id = "$year-12-08",
            name = "Inmaculada Concepción",
            date = "$year-12-08",
            type = "Religioso",
            year = year
        ))
        
        return holidays
    }
    
    /**
     * Calcular la fecha de Pascua para un año dado (algoritmo de Gauss)
     * @param year El año
     * @return Fecha de Pascua
     */
    private fun calculateEaster(year: Int): Calendar {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val n = (h + l - 7 * m + 114) / 31
        val p = (h + l - 7 * m + 114) % 31
        
        val calendar = Calendar.getInstance()
        calendar.set(year, n - 1, p + 1)
        return calendar
    }
}
