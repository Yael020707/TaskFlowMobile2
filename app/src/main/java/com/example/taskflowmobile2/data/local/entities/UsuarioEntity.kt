package com.example.taskflowmobile2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val idUsuario: Long = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val modoOscuro: Boolean = false
)
