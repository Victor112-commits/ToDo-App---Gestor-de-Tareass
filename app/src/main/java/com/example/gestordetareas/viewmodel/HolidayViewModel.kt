package com.example.gestordetareas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gestordetareas.data.HolidayEntity
import com.example.gestordetareas.repository.HolidayRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar los días feriados de Chile
 * Sigue el patrón de arquitectura MVVM
 */
class HolidayViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: HolidayRepository = HolidayRepository(application)
    
    // LiveData para observar listas de días feriados
    val allHolidays: LiveData<List<HolidayEntity>> = repository.getAllHolidays()
    
    // MutableLiveData para estado de la UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _selectedHoliday = MutableLiveData<HolidayEntity?>()
    val selectedHoliday: LiveData<HolidayEntity?> = _selectedHoliday
    
    /**
     * Obtener días feriados de un año específico
     * @param year El año a consultar
     * @return LiveData con lista de días feriados del año
     */
    fun getHolidaysByYear(year: Int): LiveData<List<HolidayEntity>> = 
        repository.getHolidaysByYear(year)
    
    /**
     * Obtener días feriados de un mes específico
     * @param year El año
     * @param month El mes (1-12)
     * @return LiveData con lista de días feriados del mes
     */
    fun getHolidaysByMonth(year: Int, month: Int): LiveData<List<HolidayEntity>> = 
        repository.getHolidaysByMonth(year, month)
    
    /**
     * Obtener un día feriado por fecha
     * @param date Fecha en formato "YYYY-MM-DD"
     */
    fun getHolidayByDate(date: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val holiday = repository.getHolidayByDate(date)
                _selectedHoliday.value = holiday
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al obtener día feriado: ${e.message}"
            }
        }
    }
    
    /**
     * Obtener días feriados de un rango de fechas
     * @param startDate Fecha de inicio en formato "YYYY-MM-DD"
     * @param endDate Fecha de fin en formato "YYYY-MM-DD"
     * @return LiveData con lista de días feriados en el rango
     */
    fun getHolidaysInRange(startDate: String, endDate: String): LiveData<List<HolidayEntity>> = 
        repository.getHolidaysInRange(startDate, endDate)
    
    /**
     * Inicializar los días feriados de Chile para un año específico
     * @param year El año a inicializar
     */
    fun initializeHolidaysForYear(year: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.initializeHolidaysForYear(year)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al inicializar días feriados: ${e.message}"
            }
        }
    }
    
    /**
     * Limpiar mensaje de error
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    /**
     * Limpiar día feriado seleccionado
     */
    fun clearSelectedHoliday() {
        _selectedHoliday.value = null
    }
}
