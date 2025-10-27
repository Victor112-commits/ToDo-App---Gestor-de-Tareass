package com.example.gestordetareas.ui

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gestordetareas.R
import com.example.gestordetareas.databinding.ActivityCalendarBinding
import com.example.gestordetareas.data.HolidayEntity
import com.example.gestordetareas.repository.HolidayRepository
import com.example.gestordetareas.viewmodel.HolidayViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Actividad para mostrar el calendario con días feriados de Chile
 * Permite visualizar los días feriados marcados en rojo
 */
class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding
    private lateinit var viewModel: HolidayViewModel
    private lateinit var calendarAdapter: CalendarAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val monthNames = arrayOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Calendario de Días Feriados"

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[HolidayViewModel::class.java]

        setupUI()
        setupObservers()
        setupCalendar()
        
        // Inicializar días feriados para el año actual
        viewModel.initializeHolidaysForYear(currentYear)
    }

    private fun setupUI() {
        // Configurar botón de año actual
        binding.buttonCurrentYear.setOnClickListener {
            currentYear = Calendar.getInstance().get(Calendar.YEAR)
            currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            viewModel.initializeHolidaysForYear(currentYear)
            updateCalendarDisplay()
        }
        
        // Configurar botón de año anterior
        binding.buttonPreviousYear.setOnClickListener {
            currentYear--
            viewModel.initializeHolidaysForYear(currentYear)
            updateCalendarDisplay()
        }
        
        // Configurar botón de año siguiente
        binding.buttonNextYear.setOnClickListener {
            currentYear++
            viewModel.initializeHolidaysForYear(currentYear)
            updateCalendarDisplay()
        }
    }
    
    private fun setupCalendar() {
        // Configurar RecyclerView del calendario
        calendarAdapter = CalendarAdapter { day ->
            if (day.isHoliday && day.holiday != null) {
                viewModel.getHolidayByDate(dateFormat.format(day.date.time))
            } else {
                viewModel.clearSelectedHoliday()
            }
        }
        
        binding.recyclerViewCalendar.apply {
            layoutManager = GridLayoutManager(this@CalendarActivity, 7)
            adapter = calendarAdapter
        }
        
        updateCalendarDisplay()
    }
    
    private fun updateCalendarDisplay() {
        binding.textViewCurrentMonth.text = "${monthNames[currentMonth]} $currentYear"
        
        // Configurar botones de navegación de mes
        binding.buttonPrevMonth.setOnClickListener {
            if (currentMonth == 0) {
                currentMonth = 11
                currentYear--
            } else {
                currentMonth--
            }
            updateCalendarDisplay()
        }
        
        binding.buttonNextMonth.setOnClickListener {
            if (currentMonth == 11) {
                currentMonth = 0
                currentYear++
            } else {
                currentMonth++
            }
            updateCalendarDisplay()
        }
    }

    private fun setupObservers() {
        // Observar días feriados del año actual
        viewModel.getHolidaysByYear(currentYear).observe(this) { holidays ->
            updateCalendarWithHolidays(holidays)
        }
        
        // Observar día feriado seleccionado
        viewModel.selectedHoliday.observe(this) { holiday ->
            updateHolidayInfo(holiday)
        }
        
        // Observar mensajes de error
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun updateCalendarWithHolidays(holidays: List<HolidayEntity>) {
        // Actualizar información de días feriados
        val holidayCount = holidays.size
        binding.textViewHolidayCount.text = "Días feriados: $holidayCount"
        
        // Mostrar lista de días feriados
        val holidayList = holidays.joinToString("\n") { holiday ->
            "${holiday.date}: ${holiday.name}"
        }
        binding.textViewHolidayList.text = holidayList
        
        // Actualizar el calendario con los días feriados
        calendarAdapter.updateCalendar(currentYear, currentMonth, holidays)
    }

    private fun updateHolidayInfo(holiday: HolidayEntity?) {
        if (holiday != null) {
            binding.layoutHolidayInfo.visibility = android.view.View.VISIBLE
            binding.textViewHolidayName.text = holiday.name
            binding.textViewHolidayType.text = "Tipo: ${holiday.type}"
            binding.textViewHolidayDate.text = "Fecha: ${holiday.date}"
        } else {
            binding.layoutHolidayInfo.visibility = android.view.View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
