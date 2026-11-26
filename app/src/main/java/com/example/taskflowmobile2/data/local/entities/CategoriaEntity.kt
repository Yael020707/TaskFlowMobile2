package com.example.taskflowmobile2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    val idCategoria: Long = 0,
    val nombre: String,
    val colorHex: String
)
