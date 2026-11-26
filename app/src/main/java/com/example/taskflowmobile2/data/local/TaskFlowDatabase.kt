package com.example.taskflowmobile2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.taskflowmobile2.data.local.dao.TaskFlowDao
import com.example.taskflowmobile2.data.local.entities.CategoriaEntity
import com.example.taskflowmobile2.data.local.entities.HitoEntity
import com.example.taskflowmobile2.data.local.entities.TareaEntity
import com.example.taskflowmobile2.data.local.entities.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        CategoriaEntity::class,
        TareaEntity::class,
        HitoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TaskFlowDatabase : RoomDatabase() {
    abstract fun taskFlowDao(): TaskFlowDao
}
