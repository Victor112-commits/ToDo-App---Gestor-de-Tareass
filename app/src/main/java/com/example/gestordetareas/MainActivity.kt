package com.example.gestordetareas

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestordetareas.data.TaskEntity
import com.example.gestordetareas.databinding.ActivityMainBinding
import com.example.gestordetareas.ui.AddEditTaskActivity
import com.example.gestordetareas.ui.CalendarActivity
import com.example.gestordetareas.ui.TrashActivity
import com.example.gestordetareas.ui.TaskListAdapter
import com.example.gestordetareas.viewmodel.TaskViewModel

/**
 * Actividad principal de la aplicación ToDo
 * Muestra la lista de tareas y maneja las interacciones del usuario
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TaskViewModel
    private lateinit var taskAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        setupUI()
        setupRecyclerView()
        setupObservers()
        setupChipGroup()
    }

    private fun setupUI() {
        // Configurar toolbar
        setSupportActionBar(binding.toolbar)

        // Configurar botones flotantes
        binding.fabAddTask.setOnClickListener {
            startActivity(Intent(this, AddEditTaskActivity::class.java))
        }
        
        binding.fabCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
        
        // Configurar búsqueda
        binding.editTextSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.setSearchQuery(s.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        // Configurar adaptador de tareas
        taskAdapter = TaskListAdapter(
            onTaskClick = { task -> onTaskClick(task) },
            onTaskToggle = { task -> onTaskToggle(task) },
            onTaskEdit = { task -> onTaskEdit(task) },
            onTaskDelete = { task -> onTaskDelete(task) }
        )

        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }

    private fun setupObservers() {
        // Observe filtered tasks
        viewModel.filteredTasks.observe(this) { tasks ->
            updateTaskList(tasks)
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun setupChipGroup() {
        // Set initial selection
        binding.chipAll.isChecked = true

        // Setup chip selection listeners
        binding.chipAll.setOnClickListener {
            viewModel.setFilter(TaskViewModel.TaskFilter.ALL)
        }

        binding.chipPending.setOnClickListener {
            viewModel.setFilter(TaskViewModel.TaskFilter.PENDING)
        }

        binding.chipCompleted.setOnClickListener {
            viewModel.setFilter(TaskViewModel.TaskFilter.COMPLETED)
        }
    }

    private fun updateTaskList(tasks: List<TaskEntity>) {
        taskAdapter.submitList(tasks)
        
        // Show/hide empty state
        if (tasks.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.recyclerViewTasks.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.recyclerViewTasks.visibility = View.VISIBLE
        }
    }

    private fun onTaskClick(task: TaskEntity) {
        // For now, just toggle completion status
        onTaskToggle(task)
    }

    private fun onTaskToggle(task: TaskEntity) {
        viewModel.toggleTaskCompletion(task)
    }

    private fun onTaskEdit(task: TaskEntity) {
        val intent = Intent(this, AddEditTaskActivity::class.java).apply {
            putExtra(AddEditTaskActivity.EXTRA_TASK_ID, task.id)
        }
        startActivity(intent)
    }

    private fun onTaskDelete(task: TaskEntity) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar tarea")
            .setMessage("¿Estás seguro de que quieres eliminar esta tarea?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_calendar -> {
                startActivity(Intent(this, CalendarActivity::class.java))
                true
            }
            R.id.action_trash -> {
                startActivity(Intent(this, TrashActivity::class.java))
                true
            }
            R.id.action_sort -> {
                showSortDialog()
                true
            }
            R.id.action_delete_completed -> {
                showDeleteCompletedDialog()
                true
            }
            R.id.action_export -> {
                showExportDialog()
                true
            }
            R.id.action_settings -> {
                // TODO: Implement settings
                Toast.makeText(this, "Configuración próximamente", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSortDialog() {
        val sortOptions = arrayOf(
            "Fecha (Más reciente)",
            "Fecha (Más antigua)",
            "Prioridad (Alta a Baja)",
            "Prioridad (Baja a Alta)",
            "Título (A-Z)",
            "Título (Z-A)"
        )
        
        val currentSort = viewModel.sortOrder.value ?: TaskViewModel.SortOrder.DATE_DESC
        val selectedIndex = when (currentSort) {
            TaskViewModel.SortOrder.DATE_DESC -> 0
            TaskViewModel.SortOrder.DATE_ASC -> 1
            TaskViewModel.SortOrder.PRIORITY_DESC -> 2
            TaskViewModel.SortOrder.PRIORITY_ASC -> 3
            TaskViewModel.SortOrder.TITLE_ASC -> 4
            TaskViewModel.SortOrder.TITLE_DESC -> 5
        }
        
        AlertDialog.Builder(this)
            .setTitle("Ordenar tareas")
            .setSingleChoiceItems(sortOptions, selectedIndex) { dialog, which ->
                val sortOrder = when (which) {
                    0 -> TaskViewModel.SortOrder.DATE_DESC
                    1 -> TaskViewModel.SortOrder.DATE_ASC
                    2 -> TaskViewModel.SortOrder.PRIORITY_DESC
                    3 -> TaskViewModel.SortOrder.PRIORITY_ASC
                    4 -> TaskViewModel.SortOrder.TITLE_ASC
                    5 -> TaskViewModel.SortOrder.TITLE_DESC
                    else -> TaskViewModel.SortOrder.DATE_DESC
                }
                viewModel.setSortOrder(sortOrder)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun showExportDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exportar tareas")
            .setMessage("¿Deseas exportar todas las tareas a un archivo de texto?")
            .setPositiveButton("Exportar") { _, _ ->
                exportTasks()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun exportTasks() {
        val tasks = viewModel.allTasks.value ?: emptyList()
        if (tasks.isEmpty()) {
            Toast.makeText(this, "No hay tareas para exportar", Toast.LENGTH_SHORT).show()
            return
        }
        
        val exportText = buildString {
            appendLine("=== MIS TAREAS ===")
            appendLine("Exportado el: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
            appendLine()
            
            tasks.forEach { task ->
                appendLine("• ${task.title}")
                if (!task.description.isNullOrEmpty()) {
                    appendLine("  Descripción: ${task.description}")
                }
                appendLine("  Prioridad: ${task.priority}")
                appendLine("  Categoría: ${task.category}")
                appendLine("  Estado: ${if (task.isCompleted) "Completada" else "Pendiente"}")
                if (task.dueDate != null) {
                    appendLine("  Fecha límite: ${com.example.gestordetareas.utils.DateUtils.formatDate(task.dueDate)}")
                }
                appendLine("  Creada: ${com.example.gestordetareas.utils.DateUtils.formatDate(task.dateCreated)}")
                appendLine()
            }
        }
        
        // Save to file
        try {
            val file = java.io.File(getExternalFilesDir(null), "tareas_exportadas.txt")
            file.writeText(exportText)
            Toast.makeText(this, "Tareas exportadas a: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDeleteCompletedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar tareas completadas")
            .setMessage("¿Estás seguro de que quieres eliminar todas las tareas completadas?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteCompletedTasks()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}