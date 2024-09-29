package com.braiso22.turnos.executions.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExecutionDao {
    @Query("SELECT * FROM ExecutionEntity")
    fun getAll(): Flow<List<ExecutionEntity>>

    @Query("SELECT * FROM ExecutionEntity WHERE taskId = :taskId")
    fun getByTaskId(taskId: String): Flow<List<ExecutionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(executions: List<ExecutionEntity>)

    @Query("DELETE FROM ExecutionEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM ExecutionEntity WHERE userId NOT LIKE :excludeUserId")
    fun getExecutionsByOtherUsers(excludeUserId: String): Flow<List<ExecutionEntity>>
}