package com.example.gestordetareas.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.gestordetareas.R
import com.example.gestordetareas.data.TaskEntity
import com.example.gestordetareas.databinding.ActivityAddEditTaskBinding
import com.example.gestordetareas.utils.DateUtils
import com.example.gestordetareas.viewmodel.TaskViewModel
import java.util.*

/**
 * Activity for adding or editing tasks
 * Handles both creation and editing of tasks
 */
class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTaskBinding
    private lateinit var viewModel: TaskViewModel
    
    private var taskId: Long = -1
    private var isEditing = false
    private var selectedDueDate: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Obtener ID de tarea del intent
        taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        isEditing = taskId != -1L

        // Actualizar UI según el modo
        if (isEditing) {
            supportActionBar?.title = "Editar Tarea"
            loadTaskData()
        } else {
            supportActionBar?.title = "Nueva Tarea"
        }

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        // Configurar spinner de prioridad
        val priorityOptions = arrayOf("Alta", "Media", "Baja")
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorityOptions)
        binding.autoCompletePriority.setAdapter(priorityAdapter)
        binding.autoCompletePriority.setText("Media", false)
        binding.autoCompletePriority.threshold = 0
        
        // Configurar spinner de categoría
        val categoryOptions = arrayOf("General", "Trabajo", "Personal", "Salud", "Estudio", "Hogar", "Finanzas")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryOptions)
        binding.autoCompleteCategory.setAdapter(categoryAdapter)
        binding.autoCompleteCategory.setText("General", false)
        binding.autoCompleteCategory.threshold = 0

        // Configurar selector de fecha de vencimiento
        binding.editTextDueDate.setOnClickListener {
            showDatePicker()
        }

        // Configurar botones de acción
        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.buttonSave.setOnClickListener {
            saveTask()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun loadTaskData() {
        if (isEditing) {
            // Cargar datos de la tarea en una corrutina
            lifecycleScope.launch {
                val task = viewModel.getTaskById(taskId)
                task?.let {
                    binding.editTextTitle.setText(it.title)
                    binding.editTextDescription.setText(it.description)
                    binding.autoCompletePriority.setText(it.priority, false)
                    binding.autoCompleteCategory.setText(it.category, false)
                    
                    if (it.dueDate != null) {
                        selectedDueDate = it.dueDate
                        binding.editTextDueDate.setText(DateUtils.formatDate(it.dueDate))
                    }
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        selectedDueDate?.let { 
            calendar.timeInMillis = it 
        }

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.CustomDatePickerDialog,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                selectedDueDate = selectedCalendar.timeInMillis
                binding.editTextDueDate.setText(DateUtils.formatDate(selectedCalendar.timeInMillis))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Establecer fecha mínima a hoy
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        
        // Personalizar colores
        datePickerDialog.window?.setBackgroundDrawableResource(android.R.color.white)
        datePickerDialog.show()
    }

    private fun saveTask() {
        val title = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val priority = binding.autoCompletePriority.text.toString()
        val category = binding.autoCompleteCategory.text.toString()

        // Validar entrada
        if (title.isEmpty()) {
            binding.editTextTitle.error = "El título es obligatorio"
            return
        }

        val task = if (isEditing) {
            TaskEntity(
                id = taskId,
                title = title,
                description = if (description.isEmpty()) null else description,
                priority = priority,
                category = category,
                dueDate = selectedDueDate,
                isCompleted = false // Lo obtendremos de la tarea existente
            )
        } else {
            TaskEntity(
                title = title,
                description = if (description.isEmpty()) null else description,
                priority = priority,
                category = category,
                dueDate = selectedDueDate
            )
        }

        if (isEditing) {
            viewModel.updateTask(task)
        } else {
            viewModel.insertTask(task)
        }

        finish()
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

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }
}
