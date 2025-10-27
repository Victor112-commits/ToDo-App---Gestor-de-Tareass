package com.example.gestordetareas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gestordetareas.data.DeletedTaskEntity
import com.example.gestordetareas.repository.DeletedTaskRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la papelera de reciclaje
 * Sigue el patrón de arquitectura MVVM
 */
class TrashViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: DeletedTaskRepository = DeletedTaskRepository(application)
    
    // LiveData para observar tareas eliminadas
    val deletedTasks: LiveData<List<DeletedTaskEntity>> = repository.getAllDeletedTasks()
    val deletedTasksCount: LiveData<Int> = repository.getDeletedTasksCount()
    
    // MutableLiveData para estado de la UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    /**
     * Obtener una tarea eliminada por su ID
     * @param id El ID de la tarea eliminada
     * @return La tarea eliminada con el ID especificado
     */
    suspend fun getDeletedTaskById(id: Long): DeletedTaskEntity? = 
        repository.getDeletedTaskById(id)
    
    /**
     * Restaurar una tarea desde la papelera de reciclaje
     * @param deletedTask La tarea eliminada a restaurar
     */
    fun restoreTask(deletedTask: DeletedTaskEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.restoreTask(deletedTask)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al restaurar tarea: ${e.message}"
            }
        }
    }
    
    /**
     * Eliminar permanentemente una tarea de la papelera
     * @param deletedTask La tarea eliminada a eliminar permanentemente
     */
    fun deletePermanently(deletedTask: DeletedTaskEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deletePermanently(deletedTask)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al eliminar permanentemente: ${e.message}"
            }
        }
    }
    
    /**
     * Eliminar permanentemente todas las tareas de la papelera
     */
    fun deleteAllPermanently() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteAllPermanently()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al limpiar papelera: ${e.message}"
            }
        }
    }
    
    /**
     * Limpiar tareas eliminadas más antiguas que la fecha especificada
     * @param olderThan Fecha límite en milisegundos
     */
    fun cleanOldDeletedTasks(olderThan: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.cleanOldDeletedTasks(olderThan)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al limpiar tareas antiguas: ${e.message}"
            }
        }
    }
    
    /**
     * Limpiar mensaje de error
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
