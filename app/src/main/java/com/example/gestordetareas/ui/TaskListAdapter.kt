package com.example.gestordetareas.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestordetareas.R
import com.example.gestordetareas.data.TaskEntity
import com.example.gestordetareas.databinding.ItemTaskBinding
import com.example.gestordetareas.utils.DateUtils

/**
 * RecyclerView adapter for displaying task list
 * Uses DiffUtil for efficient list updates
 */
class TaskListAdapter(
    private val onTaskClick: (TaskEntity) -> Unit,
    private val onTaskToggle: (TaskEntity) -> Unit,
    private val onTaskEdit: (TaskEntity) -> Unit,
    private val onTaskDelete: (TaskEntity) -> Unit
) : ListAdapter<TaskEntity, TaskListAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskEntity) {
            binding.apply {
                // Establecer datos de la tarea
                textViewTitle.text = task.title
                textViewDescription.text = task.description ?: ""
                
                // Establecer chip de prioridad
                chipPriority.text = task.priority
                chipPriority.setChipBackgroundColorResource(
                    when (task.priority) {
                        "Alta" -> R.color.priority_high
                        "Media" -> R.color.priority_medium
                        "Baja" -> R.color.priority_low
                        else -> R.color.priority_medium
                    }
                )
                
                // Establecer chip de categoría
                chipCategory.text = task.category
                chipCategory.visibility = if (task.category.isNotEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                
                // Establecer estado de completado
                checkboxCompleted.isChecked = task.isCompleted
                
                // Establecer fecha de vencimiento
                if (task.dueDate != null) {
                    textViewDueDate.text = "Vence: ${DateUtils.getRelativeDateStringWithOverdue(task.dueDate)}"
                    textViewDueDate.visibility = android.view.View.VISIBLE
                } else {
                    textViewDueDate.visibility = android.view.View.GONE
                }
                
                // Aplicar estilo de tarea completada
                if (task.isCompleted) {
                    textViewTitle.alpha = 0.6f
                    textViewDescription.alpha = 0.6f
                    textViewDueDate.alpha = 0.6f
                    root.alpha = 0.7f
                } else {
                    textViewTitle.alpha = 1.0f
                    textViewDescription.alpha = 1.0f
                    textViewDueDate.alpha = 1.0f
                    root.alpha = 1.0f
                }
                
                // Establecer listeners de clic
                root.setOnClickListener { onTaskClick(task) }
                checkboxCompleted.setOnCheckedChangeListener { _, _ ->
                    onTaskToggle(task)
                }
                buttonEdit.setOnClickListener { onTaskEdit(task) }
                buttonDelete.setOnClickListener { onTaskDelete(task) }
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates
     */
    class TaskDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}
