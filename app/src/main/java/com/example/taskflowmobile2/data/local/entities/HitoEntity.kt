package com.example.taskflowmobile2.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hitos",
    foreignKeys = [
        ForeignKey(
            entity = TareaEntity::class,
            parentColumns = ["idTarea"],
            childColumns = ["tareaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tareaId")]
)
data class HitoEntity(
    @PrimaryKey(autoGenerate = true)
    val idHito: Long = 0,
    val descripcion: String,
    val completado: Boolean = false,
    val tareaId: Long
)
