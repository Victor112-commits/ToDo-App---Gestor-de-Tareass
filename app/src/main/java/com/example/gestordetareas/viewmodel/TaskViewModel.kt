package com.example.gestordetareas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gestordetareas.data.TaskEntity
import com.example.gestordetareas.repository.TaskRepository
import com.example.gestordetareas.repository.DeletedTaskRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar datos de tareas y estado de la UI
 * Sigue el patrón de arquitectura MVVM
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TaskRepository = TaskRepository(application)
    private val deletedTaskRepository: DeletedTaskRepository = DeletedTaskRepository(application)
    
    // LiveData para observar listas de tareas
    val allTasks: LiveData<List<TaskEntity>> = repository.getAllTasks()
    val completedTasks: LiveData<List<TaskEntity>> = repository.getCompletedTasks()
    val pendingTasks: LiveData<List<TaskEntity>> = repository.getPendingTasks()
    val categories: LiveData<List<String>> = repository.getAllCategories()
    
    // MutableLiveData para estado de la UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _currentFilter = MutableLiveData<TaskFilter>()
    val currentFilter: LiveData<TaskFilter> = _currentFilter
    
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery
    
    private val _sortOrder = MutableLiveData<SortOrder>()
    val sortOrder: LiveData<SortOrder> = _sortOrder
    
    // Tareas filtradas basadas en el filtro actual
    private val _filteredTasks = MutableLiveData<List<TaskEntity>>()
    val filteredTasks: LiveData<List<TaskEntity>> = _filteredTasks
    
    init {
        _currentFilter.value = TaskFilter.ALL
        _searchQuery.value = ""
        _sortOrder.value = SortOrder.DATE_DESC
        
        // Observar todas las tareas y aplicar filtro actual
        allTasks.observeForever { tasks ->
            applyFilter(tasks)
        }
    }
    
    /**
     * Insert a new task
     * @param task The task to insert
     */
    fun insertTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertTask(task)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al crear la tarea: ${e.message}"
            }
        }
    }
    
    /**
     * Update an existing task
     * @param task The task to update
     */
    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateTask(task)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al actualizar la tarea: ${e.message}"
            }
        }
    }
    
    /**
     * Delete a task (move to trash)
     * @param task The task to delete
     */
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                deletedTaskRepository.moveToTrash(task)
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al eliminar la tarea: ${e.message}"
            }
        }
    }
    
    /**
     * Toggle task completion status
     * @param task The task to toggle
     */
    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (task.isCompleted) {
                    repository.markTaskAsPending(task.id)
                } else {
                    repository.markTaskAsCompleted(task.id)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al cambiar el estado de la tarea: ${e.message}"
            }
        }
    }
    
    /**
     * Delete all completed tasks
     */
    fun deleteCompletedTasks() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteCompletedTasks()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error al eliminar las tareas completadas: ${e.message}"
            }
        }
    }
    
    /**
     * Set the current filter for tasks
     * @param filter The filter to apply
     */
    fun setFilter(filter: TaskFilter) {
        _currentFilter.value = filter
        allTasks.value?.let { tasks ->
            applyFilter(tasks)
        }
    }
    
    /**
     * Set search query
     * @param query The search query
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        allTasks.value?.let { tasks ->
            applyFilter(tasks)
        }
    }
    
    /**
     * Set sort order
     * @param order The sort order
     */
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        allTasks.value?.let { tasks ->
            applyFilter(tasks)
        }
    }
    
    /**
     * Apply the current filter to the task list
     * @param tasks The list of tasks to filter
     */
    private fun applyFilter(tasks: List<TaskEntity>) {
        var filtered = when (_currentFilter.value) {
            TaskFilter.ALL -> tasks
            TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
            TaskFilter.HIGH_PRIORITY -> tasks.filter { it.priority == "Alta" }
            TaskFilter.MEDIUM_PRIORITY -> tasks.filter { it.priority == "Media" }
            TaskFilter.LOW_PRIORITY -> tasks.filter { it.priority == "Baja" }
            null -> tasks
        }
        
        // Aplicar filtro de búsqueda
        val searchQuery = _searchQuery.value
        if (!searchQuery.isNullOrEmpty()) {
            filtered = filtered.filter { task ->
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.description?.contains(searchQuery, ignoreCase = true) == true
            }
        }
        
        // Aplicar ordenamiento
        filtered = when (_sortOrder.value) {
            SortOrder.DATE_DESC -> filtered.sortedByDescending { it.dateCreated }
            SortOrder.DATE_ASC -> filtered.sortedBy { it.dateCreated }
            SortOrder.PRIORITY_DESC -> filtered.sortedWith(compareByDescending<TaskEntity> { 
                when(it.priority) { "Alta" -> 3; "Media" -> 2; "Baja" -> 1; else -> 0 }
            }.thenByDescending { it.dateCreated })
            SortOrder.PRIORITY_ASC -> filtered.sortedWith(compareBy<TaskEntity> { 
                when(it.priority) { "Alta" -> 3; "Media" -> 2; "Baja" -> 1; else -> 0 }
            }.thenByDescending { it.dateCreated })
            SortOrder.TITLE_ASC -> filtered.sortedBy { it.title }
            SortOrder.TITLE_DESC -> filtered.sortedByDescending { it.title }
            null -> filtered
        }
        
        _filteredTasks.value = filtered
    }
    
    /**
     * Get a task by its ID
     * @param taskId The ID of the task
     * @return The task with the specified ID
     */
    suspend fun getTaskById(taskId: Long): TaskEntity? {
        return repository.getTaskById(taskId)
    }
    
    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    /**
     * Enum for task filtering options
     */
    enum class TaskFilter {
        ALL, PENDING, COMPLETED, HIGH_PRIORITY, MEDIUM_PRIORITY, LOW_PRIORITY
    }
    
    /**
     * Enum for task sorting options
     */
    enum class SortOrder {
        DATE_DESC, DATE_ASC, PRIORITY_DESC, PRIORITY_ASC, TITLE_ASC, TITLE_DESC
    }
}
