package com.example.taskflowmobile2.ui.viewmodel

import com.example.taskflowmobile2.data.local.entities.CategoriaEntity
import com.example.taskflowmobile2.data.local.entities.HitoEntity
import com.example.taskflowmobile2.data.local.entities.TareaEntity
import com.example.taskflowmobile2.data.local.entities.UsuarioEntity

data class TaskUiState(
    val currentUser: UsuarioEntity? = null,
    val isLoggedIn: Boolean = false,
    val loginError: String? = null,
    val tareas: List<TareaEntity> = emptyList(),
    val categorias: List<CategoriaEntity> = emptyList(),
    val selectedFilter: String = "Todas", // "Todas", "Pendientes", "Completadas"
    val selectedTarea: TareaEntity? = null,
    val selectedTareaHitos: List<HitoEntity> = emptyList(),
    val isDarkMode: Boolean = false,
    val userMessage: String? = null,
    val isLoading: Boolean = false
) {
    val filteredTareas: List<TareaEntity>
        get() = when (selectedFilter) {
            "Pendientes" -> tareas.filter { !it.completada }
            "Completadas" -> tareas.filter { it.completada }
            else -> tareas
        }

    val totalTareasCount: Int
        get() = tareas.size

    val pendientesCount: Int
        get() = tareas.count { !it.completada }

    val completadasCount: Int
        get() = tareas.count { it.completada }
}
