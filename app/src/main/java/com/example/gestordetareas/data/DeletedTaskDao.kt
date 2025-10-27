package com.example.gestordetareas.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Interfaz de acceso a datos (DAO) para DeletedTaskEntity
 * Define todas las operaciones de base de datos para la papelera de reciclaje
 */
@Dao
interface DeletedTaskDao {
    
    /**
     * Insertar una tarea eliminada en la papelera
     * @param deletedTask La tarea eliminada a insertar
     * @return El ID de la tarea eliminada insertada
     */
    @Insert
    suspend fun insertDeletedTask(deletedTask: DeletedTaskEntity): Long
    
    /**
     * Obtener todas las tareas eliminadas
     * @return LiveData con lista de todas las tareas eliminadas
     */
    @Query("SELECT * FROM deleted_tasks ORDER BY deletedAt DESC")
    fun getAllDeletedTasks(): LiveData<List<DeletedTaskEntity>>
    
    /**
     * Obtener una tarea eliminada por su ID
     * @param id El ID de la tarea eliminada
     * @return La tarea eliminada con el ID especificado
     */
    @Query("SELECT * FROM deleted_tasks WHERE id = :id")
    suspend fun getDeletedTaskById(id: Long): DeletedTaskEntity?
    
    /**
     * Restaurar una tarea eliminada (convertirla de vuelta a tarea normal)
     * @param deletedTask La tarea eliminada a restaurar
     * @return El ID de la tarea restaurada
     */
    suspend fun restoreTask(deletedTask: DeletedTaskEntity): Long {
        // Esta función se implementará en el repositorio
        return 0L
    }
    
    /**
     * Eliminar permanentemente una tarea de la papelera
     * @param deletedTask La tarea eliminada a eliminar permanentemente
     */
    @Delete
    suspend fun deletePermanently(deletedTask: DeletedTaskEntity)
    
    /**
     * Eliminar permanentemente todas las tareas de la papelera
     */
    @Query("DELETE FROM deleted_tasks")
    suspend fun deleteAllPermanently()
    
    /**
     * Eliminar tareas eliminadas más antiguas que la fecha especificada
     * @param olderThan Fecha límite en milisegundos
     */
    @Query("DELETE FROM deleted_tasks WHERE deletedAt < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)
    
    /**
     * Obtener el número de tareas en la papelera
     * @return LiveData con el conteo de tareas eliminadas
     */
    @Query("SELECT COUNT(*) FROM deleted_tasks")
    fun getDeletedTasksCount(): LiveData<Int>
}
