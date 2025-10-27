package com.example.gestordetareas.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gestordetareas.R
import com.example.gestordetareas.data.HolidayEntity
import com.example.gestordetareas.databinding.ItemCalendarDayBinding
import java.util.*

/**
 * Adaptador para mostrar el calendario personalizado
 * Muestra los días del mes con días feriados marcados en rojo
 */
class CalendarAdapter(
    private val onDayClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var calendarDays = mutableListOf<CalendarDay>()
    private var holidays = listOf<HolidayEntity>()

    /**
     * Clase de datos para representar un día del calendario
     */
    data class CalendarDay(
        val day: Int,
        val isCurrentMonth: Boolean,
        val isToday: Boolean,
        val isHoliday: Boolean,
        val holiday: HolidayEntity? = null,
        val date: Calendar
    )

    fun updateCalendar(year: Int, month: Int, holidays: List<HolidayEntity>) {
        this.holidays = holidays
        calendarDays.clear()
        
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)
        
        val today = Calendar.getInstance()
        
        // Obtener el primer día del mes y calcular cuántos días del mes anterior mostrar
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val daysInPreviousMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Agregar días del mes anterior
        for (i in firstDayOfMonth - 2 downTo 0) {
            val dayCalendar = Calendar.getInstance()
            dayCalendar.set(year, month - 1, daysInPreviousMonth - i)
            calendarDays.add(CalendarDay(
                day = daysInPreviousMonth - i,
                isCurrentMonth = false,
                isToday = false,
                isHoliday = false,
                date = dayCalendar
            ))
        }
        
        // Agregar días del mes actual
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..daysInMonth) {
            val dayCalendar = Calendar.getInstance()
            dayCalendar.set(year, month, day)
            
            val dateString = String.format("%04d-%02d-%02d", year, month + 1, day)
            val holiday = holidays.find { it.date == dateString }
            
            calendarDays.add(CalendarDay(
                day = day,
                isCurrentMonth = true,
                isToday = dayCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                         dayCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                         dayCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH),
                isHoliday = holiday != null,
                holiday = holiday,
                date = dayCalendar
            ))
        }
        
        // Agregar días del mes siguiente para completar la grilla
        val remainingDays = 42 - calendarDays.size // 6 semanas x 7 días
        for (day in 1..remainingDays) {
            val dayCalendar = Calendar.getInstance()
            dayCalendar.set(year, month + 1, day)
            calendarDays.add(CalendarDay(
                day = day,
                isCurrentMonth = false,
                isToday = false,
                isHoliday = false,
                date = dayCalendar
            ))
        }
        
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(calendarDays[position])
    }

    override fun getItemCount(): Int = calendarDays.size

    inner class CalendarViewHolder(
        private val binding: ItemCalendarDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: CalendarDay) {
            binding.apply {
                textViewDay.text = day.day.toString()
                
                // Configurar colores según el estado del día
                when {
                    day.isHoliday -> {
                        // Día feriado - fondo rojo, texto blanco
                        root.setBackgroundColor(
                            ContextCompat.getColor(root.context, R.color.priority_high)
                        )
                        textViewDay.setTextColor(
                            ContextCompat.getColor(root.context, android.R.color.white)
                        )
                    }
                    day.isToday -> {
                        // Día actual - fondo azul, texto blanco
                        root.setBackgroundColor(
                            ContextCompat.getColor(root.context, R.color.md_theme_light_primary)
                        )
                        textViewDay.setTextColor(
                            ContextCompat.getColor(root.context, android.R.color.white)
                        )
                    }
                    day.isCurrentMonth -> {
                        // Día del mes actual - fondo normal, texto normal
                        root.setBackgroundColor(
                            ContextCompat.getColor(root.context, android.R.color.transparent)
                        )
                        textViewDay.setTextColor(
                            ContextCompat.getColor(root.context, R.color.md_theme_light_onSurface)
                        )
                    }
                    else -> {
                        // Día de otro mes - fondo gris, texto gris
                        root.setBackgroundColor(
                            ContextCompat.getColor(root.context, R.color.md_theme_light_surfaceVariant)
                        )
                        textViewDay.setTextColor(
                            ContextCompat.getColor(root.context, R.color.md_theme_light_onSurfaceVariant)
                        )
                    }
                }
                
                // Configurar click listener
                root.setOnClickListener {
                    onDayClick(day)
                }
            }
        }
    }
}
