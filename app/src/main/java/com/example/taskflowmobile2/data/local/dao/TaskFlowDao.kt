package com.example.taskflowmobile2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskflowmobile2.data.local.entities.CategoriaEntity
import com.example.taskflowmobile2.data.local.entities.HitoEntity
import com.example.taskflowmobile2.data.local.entities.TareaEntity
import com.example.taskflowmobile2.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskFlowDao {
    // --- USUARIO ---
    @Query("SELECT * FROM usuarios WHERE LOWER(email) = LOWER(:email) OR LOWER(nombre) = LOWER(:email) LIMIT 1")
    suspend fun getUsuarioByEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE idUsuario = :id LIMIT 1")
    suspend fun getUsuarioById(id: Long): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun getUsuarioCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity): Long

    @Update
    suspend fun updateUsuario(usuario: UsuarioEntity): Int

    @Delete
    suspend fun deleteUsuario(usuario: UsuarioEntity): Int

    // --- CATEGORIA ---
    @Query("SELECT * FROM categorias")
    fun getCategorias(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias")
    suspend fun getCategoriasList(): List<CategoriaEntity>

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun getCategoriaCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoria(categoria: CategoriaEntity): Long

    @Update
    suspend fun updateCategoria(categoria: CategoriaEntity): Int

    @Delete
    suspend fun deleteCategoria(categoria: CategoriaEntity): Int

    // --- TAREA ---
    @Query("SELECT * FROM tareas ORDER BY idTarea DESC")
    fun getTareas(): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE idTarea = :idTarea LIMIT 1")
    suspend fun getTareaById(idTarea: Long): TareaEntity?

    @Query("SELECT COUNT(*) FROM tareas")
    suspend fun getTareaCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarea(tarea: TareaEntity): Long

    @Update
    suspend fun updateTarea(tarea: TareaEntity): Int

    @Delete
    suspend fun deleteTarea(tarea: TareaEntity): Int

    // --- HITO ---
    @Query("SELECT * FROM hitos WHERE tareaId = :tareaId ORDER BY idHito ASC")
    fun getHitosByTarea(tareaId: Long): Flow<List<HitoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHito(hito: HitoEntity): Long

    @Update
    suspend fun updateHito(hito: HitoEntity): Int

    @Delete
    suspend fun deleteHito(hito: HitoEntity): Int
}
