package com.example.gestordetareas.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestordetareas.R
import com.example.gestordetareas.data.DeletedTaskEntity
import com.example.gestordetareas.databinding.ItemTrashTaskBinding
import com.example.gestordetareas.utils.DateUtils

/**
 * Adaptador de RecyclerView para mostrar la lista de tareas eliminadas
 * Utiliza DiffUtil para actualizaciones eficientes de la lista
 */
class TrashListAdapter(
    private val onTaskRestore: (DeletedTaskEntity) -> Unit,
    private val onTaskDeletePermanently: (DeletedTaskEntity) -> Unit
) : ListAdapter<DeletedTaskEntity, TrashListAdapter.TrashViewHolder>(TrashDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
        val binding = ItemTrashTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrashViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrashViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrashViewHolder(
        private val binding: ItemTrashTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: DeletedTaskEntity) {
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
                    textViewDueDate.text = "Vencía: ${DateUtils.getRelativeDateStringWithOverdue(task.dueDate)}"
                    textViewDueDate.visibility = android.view.View.VISIBLE
                } else {
                    textViewDueDate.visibility = android.view.View.GONE
                }
                
                // Establecer fecha de eliminación
                textViewDeletedDate.text = "Eliminada: ${DateUtils.formatDateTime(task.deletedAt)}"
                
                // Aplicar estilo de tarea eliminada
                textViewTitle.alpha = 0.7f
                textViewDescription.alpha = 0.7f
                textViewDueDate.alpha = 0.7f
                root.alpha = 0.8f
                
                // Establecer listeners de clic
                buttonRestore.setOnClickListener { onTaskRestore(task) }
                buttonDeletePermanently.setOnClickListener { onTaskDeletePermanently(task) }
            }
        }
    }

    /**
     * Callback de DiffUtil para actualizaciones eficientes de la lista
     */
    class TrashDiffCallback : DiffUtil.ItemCallback<DeletedTaskEntity>() {
        override fun areItemsTheSame(oldItem: DeletedTaskEntity, newItem: DeletedTaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DeletedTaskEntity, newItem: DeletedTaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}
