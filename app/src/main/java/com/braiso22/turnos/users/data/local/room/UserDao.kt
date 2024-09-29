package com.braiso22.turnos.users.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("SELECT * FROM UserEntity")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM UserEntity WHERE id like :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM UserEntity WHERE email like :email")
    suspend fun getUserByEmail(email: String): UserEntity?

}