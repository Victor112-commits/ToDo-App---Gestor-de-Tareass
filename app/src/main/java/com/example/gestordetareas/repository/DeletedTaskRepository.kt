package com.example.gestordetareas.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.gestordetareas.data.DeletedTaskDao
import com.example.gestordetareas.data.DeletedTaskEntity
import com.example.gestordetareas.data.TaskDatabase
import com.example.gestordetareas.data.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para manejar la papelera de reciclaje
 * Proporciona una API limpia para el acceso a datos de tareas eliminadas
 */
class DeletedTaskRepository(application: Application) {
    
    private val deletedTaskDao: DeletedTaskDao = TaskDatabase.getDatabase(application).deletedTaskDao()
    private val taskDao = TaskDatabase.getDatabase(application).taskDao()
    
    /**
     * Obtener todas las tareas eliminadas
     * @return LiveData con lista de todas las tareas eliminadas
     */
    fun getAllDeletedTasks(): LiveData<List<DeletedTaskEntity>> = deletedTaskDao.getAllDeletedTasks()
    
    /**
     * Obtener una tarea eliminada por su ID
     * @param id El ID de la tarea eliminada
     * @return La tarea eliminada con el ID especificado
     */
    suspend fun getDeletedTaskById(id: Long): DeletedTaskEntity? = withContext(Dispatchers.IO) {
        deletedTaskDao.getDeletedTaskById(id)
    }
    
    /**
     * Mover una tarea a la papelera de reciclaje
     * @param task La tarea a eliminar
     */
    suspend fun moveToTrash(task: TaskEntity) = withContext(Dispatchers.IO) {
        // Crear entidad de tarea eliminada
        val deletedTask = DeletedTaskEntity(
            originalTaskId = task.id,
            title = task.title,
            description = task.description,
            priority = task.priority,
            category = task.category,
            isCompleted = task.isCompleted,
            dateCreated = task.dateCreated,
            dueDate = task.dueDate
        )
        
        // Insertar en papelera y eliminar de tareas normales
        deletedTaskDao.insertDeletedTask(deletedTask)
        taskDao.deleteTask(task)
    }
    
    /**
     * Restaurar una tarea desde la papelera de reciclaje
     * @param deletedTask La tarea eliminada a restaurar
     * @return El ID de la tarea restaurada
     */
    suspend fun restoreTask(deletedTask: DeletedTaskEntity): Long = withContext(Dispatchers.IO) {
        // Crear nueva tarea desde la tarea eliminada
        val restoredTask = TaskEntity(
            title = deletedTask.title,
            description = deletedTask.description,
            priority = deletedTask.priority,
            category = deletedTask.category,
            isCompleted = deletedTask.isCompleted,
            dateCreated = deletedTask.dateCreated,
            dueDate = deletedTask.dueDate
        )
        
        // Insertar como tarea normal y eliminar de papelera
        val newTaskId = taskDao.insertTask(restoredTask)
        deletedTaskDao.deletePermanently(deletedTask)
        
        newTaskId
    }
    
    /**
     * Eliminar permanentemente una tarea de la papelera
     * @param deletedTask La tarea eliminada a eliminar permanentemente
     */
    suspend fun deletePermanently(deletedTask: DeletedTaskEntity) = withContext(Dispatchers.IO) {
        deletedTaskDao.deletePermanently(deletedTask)
    }
    
    /**
     * Eliminar permanentemente todas las tareas de la papelera
     */
    suspend fun deleteAllPermanently() = withContext(Dispatchers.IO) {
        deletedTaskDao.deleteAllPermanently()
    }
    
    /**
     * Limpiar tareas eliminadas más antiguas que la fecha especificada
     * @param olderThan Fecha límite en milisegundos
     */
    suspend fun cleanOldDeletedTasks(olderThan: Long) = withContext(Dispatchers.IO) {
        deletedTaskDao.deleteOlderThan(olderThan)
    }
    
    /**
     * Obtener el número de tareas en la papelera
     * @return LiveData con el conteo de tareas eliminadas
     */
    fun getDeletedTasksCount(): LiveData<Int> = deletedTaskDao.getDeletedTasksCount()
}
