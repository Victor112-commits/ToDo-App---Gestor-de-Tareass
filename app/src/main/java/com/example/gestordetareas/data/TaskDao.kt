package com.example.gestordetareas.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Interfaz de Objeto de Acceso a Datos (DAO) para TaskEntity
 * Define todas las operaciones de base de datos para tareas
 */
@Dao
interface TaskDao {
    
    /**
     * Insertar una nueva tarea en la base de datos
     * @param task La tarea a insertar
     * @return El ID de la tarea insertada
     */
    @Insert
    suspend fun insertTask(task: TaskEntity): Long
    
    /**
     * Actualizar una tarea existente
     * @param task La tarea a actualizar
     */
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    /**
     * Eliminar una tarea de la base de datos
     * @param task La tarea a eliminar
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    /**
     * Obtener todas las tareas ordenadas por fecha de creación (más recientes primero)
     * @return LiveData con lista de todas las tareas
     */
    @Query("SELECT * FROM tasks ORDER BY dateCreated DESC")
    fun getAllTasks(): LiveData<List<TaskEntity>>
    
    /**
     * Obtener todas las tareas completadas
     * @return LiveData con lista de tareas completadas
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY dateCreated DESC")
    fun getCompletedTasks(): LiveData<List<TaskEntity>>
    
    /**
     * Obtener todas las tareas pendientes
     * @return LiveData con lista de tareas pendientes
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dateCreated DESC")
    fun getPendingTasks(): LiveData<List<TaskEntity>>
    
    /**
     * Get tasks by priority
     * @param priority The priority level (Alta, Media, Baja)
     * @return LiveData list of tasks with the specified priority
     */
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dateCreated DESC")
    fun getTasksByPriority(priority: String): LiveData<List<TaskEntity>>
    
    /**
     * Get tasks by category
     * @param category The category name
     * @return LiveData list of tasks with the specified category
     */
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY dateCreated DESC")
    fun getTasksByCategory(category: String): LiveData<List<TaskEntity>>
    
    /**
     * Search tasks by title or description
     * @param searchQuery The search query
     * @return LiveData list of matching tasks
     */
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%' ORDER BY dateCreated DESC")
    fun searchTasks(searchQuery: String): LiveData<List<TaskEntity>>
    
    /**
     * Get all unique categories
     * @return LiveData list of unique categories
     */
    @Query("SELECT DISTINCT category FROM tasks ORDER BY category ASC")
    fun getAllCategories(): LiveData<List<String>>
    
    /**
     * Mark a task as completed
     * @param taskId The ID of the task to mark as completed
     */
    @Query("UPDATE tasks SET isCompleted = 1 WHERE id = :taskId")
    suspend fun markTaskAsCompleted(taskId: Long)
    
    /**
     * Mark a task as pending
     * @param taskId The ID of the task to mark as pending
     */
    @Query("UPDATE tasks SET isCompleted = 0 WHERE id = :taskId")
    suspend fun markTaskAsPending(taskId: Long)
    
    /**
     * Delete all completed tasks
     */
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()
    
    /**
     * Get a task by its ID
     * @param taskId The ID of the task
     * @return The task with the specified ID
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?
}
