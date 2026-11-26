package com.example.taskflowmobile2.di

import android.content.Context
import androidx.room.Room
import com.example.taskflowmobile2.data.local.TaskFlowDatabase
import com.example.taskflowmobile2.data.local.dao.TaskFlowDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TaskFlowDatabase {
        return Room.databaseBuilder(
            context,
            TaskFlowDatabase::class.java,
            "taskflow_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTaskFlowDao(database: TaskFlowDatabase): TaskFlowDao {
        return database.taskFlowDao()
    }
}
