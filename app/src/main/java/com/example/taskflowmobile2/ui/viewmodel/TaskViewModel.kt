package com.example.taskflowmobile2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflowmobile2.data.local.entities.HitoEntity
import com.example.taskflowmobile2.data.local.entities.TareaEntity
import com.example.taskflowmobile2.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialData()
            observeDatabase()
        }
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            repository.getTareas().collectLatest { tareas ->
                _uiState.update { currentState ->
                    val updatedSelected = currentState.selectedTarea?.let { sel ->
                        tareas.find { it.idTarea == sel.idTarea }
                    }
                    currentState.copy(
                        tareas = tareas,
                        selectedTarea = updatedSelected ?: currentState.selectedTarea
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getCategorias().collectLatest { categorias ->
                _uiState.update { it.copy(categorias = categorias) }
            }
        }
    }

    fun login(emailInput: String, passwordInput: String, onSuccess: () -> Unit) {
        val email = emailInput.trim()
        val password = passwordInput.trim()

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(loginError = "Por favor, ingresa el usuario/correo y la contraseña.") }
            return
        }

        viewModelScope.launch {
            try {
                val user = repository.getUsuarioByEmail(email)
                if (user != null && user.password == password) {
                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            isLoggedIn = true,
                            loginError = null,
                            isDarkMode = user.modoOscuro,
                            userMessage = "¡Bienvenido/a, ${user.nombre}!"
                        )
                    }
                    onSuccess()
                } else {
                    _uiState.update {
                        it.copy(loginError = "Credenciales incorrectas. (Prueba: kevin / 5669)")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loginError = "Error al iniciar sesión: ${e.localizedMessage}") }
            }
        }
    }

    fun logout(onLogout: () -> Unit) {
        _uiState.update {
            TaskUiState(
                tareas = it.tareas,
                categorias = it.categorias,
                isDarkMode = it.isDarkMode
            )
        }
        onLogout()
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun toggleTaskCompletion(tarea: TareaEntity) {
        viewModelScope.launch {
            try {
                val updated = tarea.copy(completada = !tarea.completada)
                repository.updateTarea(updated)
                val statusText = if (updated.completada) "completada" else "marcada como pendiente"
                _uiState.update { it.copy(userMessage = "Tarea '$statusText'") }
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Error al actualizar tarea: ${e.localizedMessage}") }
            }
        }
    }

    fun createTask(
        titulo: String,
        descripcion: String,
        prioridad: String,
        fechaLimite: String,
        categoriaId: Long,
        onCreated: () -> Unit
    ) {
        if (titulo.isBlank()) {
            _uiState.update { it.copy(userMessage = "El título de la tarea no puede estar vacío.") }
            return
        }

        viewModelScope.launch {
            try {
                val nuevaTarea = TareaEntity(
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim(),
                    prioridad = prioridad.ifBlank { "Media" },
                    completada = false,
                    fechaLimite = fechaLimite.ifBlank { "Sin fecha" },
                    categoriaId = if (categoriaId > 0) categoriaId else (_uiState.value.categorias.firstOrNull()?.idCategoria ?: 1L)
                )
                repository.insertTarea(nuevaTarea)
                _uiState.update { it.copy(userMessage = "Tarea creada exitosamente.") }
                onCreated()
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Error al crear tarea: ${e.localizedMessage}") }
            }
        }
    }

    fun updateTask(tarea: TareaEntity, onUpdated: () -> Unit) {
        if (tarea.titulo.isBlank()) {
            _uiState.update { it.copy(userMessage = "El título no puede estar vacío.") }
            return
        }
        viewModelScope.launch {
            try {
                repository.updateTarea(tarea)
                _uiState.update {
                    it.copy(
                        selectedTarea = tarea,
                        userMessage = "Tarea actualizada correctamente."
                    )
                }
                onUpdated()
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Error al actualizar tarea: ${e.localizedMessage}") }
            }
        }
    }

    fun deleteTask(tarea: TareaEntity) {
        viewModelScope.launch {
            try {
                repository.deleteTarea(tarea)
                _uiState.update {
                    val newSelected = if (it.selectedTarea?.idTarea == tarea.idTarea) null else it.selectedTarea
                    it.copy(
                        selectedTarea = newSelected,
                        userMessage = "Tarea '${tarea.titulo}' eliminada."
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Error al eliminar tarea: ${e.localizedMessage}") }
            }
        }
    }

    fun selectTask(taskId: Long) {
        viewModelScope.launch {
            val tarea = repository.getTareaById(taskId)
            _uiState.update { it.copy(selectedTarea = tarea) }
            if (tarea != null) {
                repository.getHitosByTarea(taskId).collectLatest { hitos ->
                    _uiState.update { it.copy(selectedTareaHitos = hitos) }
                }
            }
        }
    }

    fun addHito(tareaId: Long, descripcion: String, onAdded: () -> Unit = {}) {
        val desc = descripcion.trim()
        if (desc.isBlank()) {
            _uiState.update { it.copy(userMessage = "La descripción del hito no puede estar vacía.") }
            return
        }
        viewModelScope.launch {
            try {
                repository.insertHito(
                    HitoEntity(
                        descripcion = desc,
                        completado = false,
                        tareaId = tareaId
                    )
                )
                _uiState.update { it.copy(userMessage = "Hito agregado.") }
                onAdded()
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Error al agregar hito: ${e.localizedMessage}") }
            }
        }
    }

    fun toggleHitoCompletion(hito: HitoEntity) {
        viewModelScope.launch {
            try {
                val updated = hito.copy(completado = !hito.completado)
                repository.updateHito(updated)
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Error al actualizar hito: ${e.localizedMessage}") }
            }
        }
    }

    fun deleteHito(hito: HitoEntity) {
        viewModelScope.launch {
            try {
                repository.deleteHito(hito)
                _uiState.update { it.copy(userMessage = "Hito eliminado.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Error al eliminar hito: ${e.localizedMessage}") }
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
        viewModelScope.launch {
            val user = _uiState.value.currentUser
            if (user != null) {
                val updatedUser = user.copy(modoOscuro = enabled)
                repository.updateUsuario(updatedUser)
                _uiState.update { it.copy(currentUser = updatedUser) }
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
