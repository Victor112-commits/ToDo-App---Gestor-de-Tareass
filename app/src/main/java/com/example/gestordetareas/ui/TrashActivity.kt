package com.example.gestordetareas.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestordetareas.R
import com.example.gestordetareas.data.DeletedTaskEntity
import com.example.gestordetareas.databinding.ActivityTrashBinding
import com.example.gestordetareas.viewmodel.TrashViewModel

/**
 * Actividad para mostrar la papelera de reciclaje
 * Permite restaurar o eliminar permanentemente tareas eliminadas
 */
class TrashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrashBinding
    private lateinit var viewModel: TrashViewModel
    private lateinit var trashAdapter: TrashListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Papelera de Reciclaje"

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[TrashViewModel::class.java]

        setupUI()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupUI() {
        // Configurar botón de limpiar todo
        binding.buttonCleanAll.setOnClickListener {
            showCleanAllDialog()
        }
    }

    private fun setupRecyclerView() {
        trashAdapter = TrashListAdapter(
            onTaskRestore = { task -> onTaskRestore(task) },
            onTaskDeletePermanently = { task -> onTaskDeletePermanently(task) }
        )

        binding.recyclerViewTrash.apply {
            layoutManager = LinearLayoutManager(this@TrashActivity)
            adapter = trashAdapter
        }
    }

    private fun setupObservers() {
        // Observar tareas eliminadas
        viewModel.deletedTasks.observe(this) { tasks ->
            updateTaskList(tasks)
        }

        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observar mensajes de error
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }

        // Observar conteo de tareas eliminadas
        viewModel.deletedTasksCount.observe(this) { count ->
            binding.textViewTrashCount.text = "Tareas eliminadas: $count"
        }
    }

    private fun updateTaskList(tasks: List<DeletedTaskEntity>) {
        trashAdapter.submitList(tasks)
        
        // Mostrar/ocultar estado vacío
        if (tasks.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.recyclerViewTrash.visibility = View.GONE
            binding.buttonCleanAll.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.recyclerViewTrash.visibility = View.VISIBLE
            binding.buttonCleanAll.visibility = View.VISIBLE
        }
    }

    private fun onTaskRestore(task: DeletedTaskEntity) {
        AlertDialog.Builder(this)
            .setTitle("Restaurar tarea")
            .setMessage("¿Estás seguro de que quieres restaurar esta tarea?")
            .setPositiveButton("Restaurar") { _, _ ->
                viewModel.restoreTask(task)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun onTaskDeletePermanently(task: DeletedTaskEntity) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar permanentemente")
            .setMessage("¿Estás seguro de que quieres eliminar permanentemente esta tarea? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deletePermanently(task)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showCleanAllDialog() {
        AlertDialog.Builder(this)
            .setTitle("Limpiar papelera")
            .setMessage("¿Estás seguro de que quieres eliminar permanentemente todas las tareas de la papelera? Esta acción no se puede deshacer.")
            .setPositiveButton("Limpiar todo") { _, _ ->
                viewModel.deleteAllPermanently()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.trash_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clean_old -> {
                showCleanOldDialog()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCleanOldDialog() {
        AlertDialog.Builder(this)
            .setTitle("Limpiar tareas antiguas")
            .setMessage("¿Eliminar tareas eliminadas hace más de 30 días?")
            .setPositiveButton("Limpiar") { _, _ ->
                val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
                viewModel.cleanOldDeletedTasks(thirtyDaysAgo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
