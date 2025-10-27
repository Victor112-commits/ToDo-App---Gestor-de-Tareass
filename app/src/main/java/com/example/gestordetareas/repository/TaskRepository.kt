package com.example.gestordetareas.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.gestordetareas.data.TaskDao
import com.example.gestordetareas.data.TaskDatabase
import com.example.gestordetareas.data.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository class that provides a clean API for data access
 * Acts as a single source of truth for task data
 */
class TaskRepository(application: Application) {
    
    private val taskDao: TaskDao = TaskDatabase.getDatabase(application).taskDao()
    
    /**
     * Get all tasks
     * @return LiveData list of all tasks
     */
    fun getAllTasks(): LiveData<List<TaskEntity>> = taskDao.getAllTasks()
    
    /**
     * Get completed tasks
     * @return LiveData list of completed tasks
     */
    fun getCompletedTasks(): LiveData<List<TaskEntity>> = taskDao.getCompletedTasks()
    
    /**
     * Get pending tasks
     * @return LiveData list of pending tasks
     */
    fun getPendingTasks(): LiveData<List<TaskEntity>> = taskDao.getPendingTasks()
    
    /**
     * Get tasks by priority
     * @param priority The priority level
     * @return LiveData list of tasks with the specified priority
     */
    fun getTasksByPriority(priority: String): LiveData<List<TaskEntity>> = 
        taskDao.getTasksByPriority(priority)
    
    /**
     * Get tasks by category
     * @param category The category name
     * @return LiveData list of tasks with the specified category
     */
    fun getTasksByCategory(category: String): LiveData<List<TaskEntity>> = 
        taskDao.getTasksByCategory(category)
    
    /**
     * Search tasks by title or description
     * @param searchQuery The search query
     * @return LiveData list of matching tasks
     */
    fun searchTasks(searchQuery: String): LiveData<List<TaskEntity>> = 
        taskDao.searchTasks(searchQuery)
    
    /**
     * Get all unique categories
     * @return LiveData list of unique categories
     */
    fun getAllCategories(): LiveData<List<String>> = 
        taskDao.getAllCategories()
    
    /**
     * Insert a new task
     * @param task The task to insert
     * @return The ID of the inserted task
     */
    suspend fun insertTask(task: TaskEntity): Long = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }
    
    /**
     * Update an existing task
     * @param task The task to update
     */
    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
    }
    
    /**
     * Delete a task
     * @param task The task to delete
     */
    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
    }
    
    /**
     * Mark a task as completed
     * @param taskId The ID of the task to mark as completed
     */
    suspend fun markTaskAsCompleted(taskId: Long) = withContext(Dispatchers.IO) {
        taskDao.markTaskAsCompleted(taskId)
    }
    
    /**
     * Mark a task as pending
     * @param taskId The ID of the task to mark as pending
     */
    suspend fun markTaskAsPending(taskId: Long) = withContext(Dispatchers.IO) {
        taskDao.markTaskAsPending(taskId)
    }
    
    /**
     * Delete all completed tasks
     */
    suspend fun deleteCompletedTasks() = withContext(Dispatchers.IO) {
        taskDao.deleteCompletedTasks()
    }
    
    /**
     * Get a task by its ID
     * @param taskId The ID of the task
     * @return The task with the specified ID
     */
    suspend fun getTaskById(taskId: Long): TaskEntity? = withContext(Dispatchers.IO) {
        taskDao.getTaskById(taskId)
    }
}
