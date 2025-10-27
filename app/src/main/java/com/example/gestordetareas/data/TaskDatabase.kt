package com.example.gestordetareas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database class for the ToDo app
 * Manages the database instance and provides access to DAOs
 */
@Database(
    entities = [TaskEntity::class, HolidayEntity::class, DeletedTaskEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {
    
    /**
     * Proporciona acceso al TaskDao
     * @return Instancia de TaskDao
     */
    abstract fun taskDao(): TaskDao
    
    /**
     * Proporciona acceso al HolidayDao
     * @return Instancia de HolidayDao
     */
    abstract fun holidayDao(): HolidayDao
    
    /**
     * Proporciona acceso al DeletedTaskDao
     * @return Instancia de DeletedTaskDao
     */
    abstract fun deletedTaskDao(): DeletedTaskDao
    
    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null
        
        /**
         * Obtiene la instancia de la base de datos usando el patrón Singleton
         * @param context Contexto de la aplicación
         * @return Instancia de TaskDatabase
         */
        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
