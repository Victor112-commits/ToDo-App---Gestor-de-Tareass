package com.example.gestordetareas.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for date formatting and manipulation
 */
object DateUtils {
    
    private const val DATE_FORMAT = "dd/MM/yyyy"
    private const val DATETIME_FORMAT = "dd/MM/yyyy HH:mm"
    
    private val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    private val dateTimeFormatter = SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault())
    
    /**
     * Format a timestamp to a readable date string
     * @param timestamp The timestamp to format
     * @return Formatted date string
     */
    fun formatDate(timestamp: Long): String {
        return dateFormatter.format(Date(timestamp))
    }
    
    /**
     * Format a timestamp to a readable date and time string
     * @param timestamp The timestamp to format
     * @return Formatted date and time string
     */
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormatter.format(Date(timestamp))
    }
    
    /**
     * Get current timestamp
     * @return Current timestamp in milliseconds
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    /**
     * Check if a date is today
     * @param timestamp The timestamp to check
     * @return True if the date is today
     */
    fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }
    
    /**
     * Check if a date is tomorrow
     * @param timestamp The timestamp to check
     * @return True if the date is tomorrow
     */
    fun isTomorrow(timestamp: Long): Boolean {
        val tomorrow = Calendar.getInstance().apply { 
            add(Calendar.DAY_OF_YEAR, 1) 
        }
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return tomorrow.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                tomorrow.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }
    
    /**
     * Check if a date is overdue
     * @param timestamp The timestamp to check
     * @return True if the date is in the past
     */
    fun isOverdue(timestamp: Long): Boolean {
        return timestamp < System.currentTimeMillis()
    }
    
    /**
     * Get a relative date string (Today, Tomorrow, or formatted date)
     * @param timestamp The timestamp to format
     * @return Relative date string
     */
    fun getRelativeDateString(timestamp: Long): String {
        return when {
            isToday(timestamp) -> "Hoy"
            isTomorrow(timestamp) -> "Mañana"
            else -> formatDate(timestamp)
        }
    }
    
    /**
     * Get a relative date string with overdue indicator
     * @param timestamp The timestamp to format
     * @return Relative date string with overdue indicator if applicable
     */
    fun getRelativeDateStringWithOverdue(timestamp: Long): String {
        val relativeDate = getRelativeDateString(timestamp)
        return if (isOverdue(timestamp)) {
            "$relativeDate (Vencida)"
        } else {
            relativeDate
        }
    }
}
