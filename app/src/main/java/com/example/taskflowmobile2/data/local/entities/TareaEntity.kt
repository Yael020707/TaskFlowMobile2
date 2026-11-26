package com.example.taskflowmobile2.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["idCategoria"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoriaId")]
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val idTarea: Long = 0,
    val titulo: String,
    val descripcion: String,
    val prioridad: String, // "Alta", "Media", "Baja"
    val completada: Boolean = false,
    val fechaLimite: String,
    val categoriaId: Long
)
