package com.example.taskflowmobile2.data.repository

import com.example.taskflowmobile2.data.local.dao.TaskFlowDao
import com.example.taskflowmobile2.data.local.entities.CategoriaEntity
import com.example.taskflowmobile2.data.local.entities.HitoEntity
import com.example.taskflowmobile2.data.local.entities.TareaEntity
import com.example.taskflowmobile2.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val dao: TaskFlowDao
) {
    // Seed initial data if database is empty
    suspend fun seedInitialData() {
        if (dao.getCategoriaCount() == 0) {
            val generalId = dao.insertCategoria(CategoriaEntity(nombre = "General", colorHex = "#6750A4"))
            val trabajoId = dao.insertCategoria(CategoriaEntity(nombre = "Trabajo", colorHex = "#0061A4"))
            val uniId = dao.insertCategoria(CategoriaEntity(nombre = "Universidad", colorHex = "#984061"))
            dao.insertCategoria(CategoriaEntity(nombre = "Personal", colorHex = "#3B6939"))

            if (dao.getTareaCount() == 0) {
                val tarea1Id = dao.insertTarea(
                    TareaEntity(
                        titulo = "Proyecto Final de Android",
                        descripcion = "Desarrollar la app TaskFlow Mobile con Room, Hilt y Jetpack Compose",
                        prioridad = "Alta",
                        completada = false,
                        fechaLimite = "2026-03-30",
                        categoriaId = uniId
                    )
                )

                dao.insertHito(HitoEntity(descripcion = "Configurar Room y Hilt", completado = true, tareaId = tarea1Id))
                dao.insertHito(HitoEntity(descripcion = "Diseñar UI en Compose", completado = true, tareaId = tarea1Id))
                dao.insertHito(HitoEntity(descripcion = "Pruebas y entrega", completado = false, tareaId = tarea1Id))

                dao.insertTarea(
                    TareaEntity(
                        titulo = "Reunión de equipo",
                        descripcion = "Revisión de avance semanal de requerimientos",
                        prioridad = "Media",
                        completada = true,
                        fechaLimite = "2026-03-25",
                        categoriaId = trabajoId
                    )
                )

                dao.insertTarea(
                    TareaEntity(
                        titulo = "Comprar insumos",
                        descripcion = "Artículos de papelería para el semestre",
                        prioridad = "Baja",
                        completada = false,
                        fechaLimite = "2026-04-05",
                        categoriaId = generalId
                    )
                )
            }
        }

        // Seed default users if not existing
        if (dao.getUsuarioByEmail("kevin") == null) {
            dao.insertUsuario(
                UsuarioEntity(
                    nombre = "Kevin",
                    email = "kevin",
                    password = "5669",
                    modoOscuro = false
                )
            )
        }

        if (dao.getUsuarioByEmail("maria@uttt.edu.mx") == null) {
            dao.insertUsuario(
                UsuarioEntity(
                    nombre = "María García",
                    email = "maria@uttt.edu.mx",
                    password = "123456",
                    modoOscuro = false
                )
            )
        }
    }

    // Usuario
    suspend fun getUsuarioByEmail(email: String): UsuarioEntity? {
        return dao.getUsuarioByEmail(email)
    }

    suspend fun getUsuarioById(id: Long): UsuarioEntity? {
        return dao.getUsuarioById(id)
    }

    suspend fun insertUsuario(usuario: UsuarioEntity): Long {
        return dao.insertUsuario(usuario)
    }

    suspend fun updateUsuario(usuario: UsuarioEntity) {
        dao.updateUsuario(usuario)
    }

    suspend fun deleteUsuario(usuario: UsuarioEntity) {
        dao.deleteUsuario(usuario)
    }

    // Categorias
    fun getCategorias(): Flow<List<CategoriaEntity>> {
        return dao.getCategorias()
    }

    suspend fun getCategoriasList(): List<CategoriaEntity> {
        return dao.getCategoriasList()
    }

    suspend fun insertCategoria(categoria: CategoriaEntity): Long {
        return dao.insertCategoria(categoria)
    }

    suspend fun updateCategoria(categoria: CategoriaEntity) {
        dao.updateCategoria(categoria)
    }

    suspend fun deleteCategoria(categoria: CategoriaEntity) {
        dao.deleteCategoria(categoria)
    }

    // Tareas
    fun getTareas(): Flow<List<TareaEntity>> {
        return dao.getTareas()
    }

    suspend fun getTareaById(idTarea: Long): TareaEntity? {
        return dao.getTareaById(idTarea)
    }

    suspend fun insertTarea(tarea: TareaEntity): Long {
        return dao.insertTarea(tarea)
    }

    suspend fun updateTarea(tarea: TareaEntity) {
        dao.updateTarea(tarea)
    }

    suspend fun deleteTarea(tarea: TareaEntity) {
        dao.deleteTarea(tarea)
    }

    // Hitos
    fun getHitosByTarea(tareaId: Long): Flow<List<HitoEntity>> {
        return dao.getHitosByTarea(tareaId)
    }

    suspend fun insertHito(hito: HitoEntity): Long {
        return dao.insertHito(hito)
    }

    suspend fun updateHito(hito: HitoEntity) {
        dao.updateHito(hito)
    }

    suspend fun deleteHito(hito: HitoEntity) {
        dao.deleteHito(hito)
    }
}
