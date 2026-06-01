package com.example.data.Finance.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.Finance.datasource.local.model.GoalLocalEntity

@Dao
interface GoalDao {

    @Query("SELECT * FROM fin_goals WHERE userId = :userId AND pendingDelete = 0 ORDER BY id ASC")
    suspend fun getAll(userId: Int): List<GoalLocalEntity>

    @Query("SELECT * FROM fin_goals WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): GoalLocalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: GoalLocalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<GoalLocalEntity>)

    @Query("DELETE FROM fin_goals WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM fin_goals WHERE pendingCreate = 1 AND userId = :userId")
    suspend fun pendingCreate(userId: Int): List<GoalLocalEntity>

    @Query("SELECT * FROM fin_goals WHERE pendingUpdate = 1 AND userId = :userId")
    suspend fun pendingUpdate(userId: Int): List<GoalLocalEntity>

    @Query("SELECT * FROM fin_goals WHERE pendingDelete = 1 AND userId = :userId")
    suspend fun pendingDelete(userId: Int): List<GoalLocalEntity>

    @Query("DELETE FROM fin_goals WHERE userId = :userId AND pendingCreate = 0 AND pendingDelete = 0 AND pendingUpdate = 0")
    suspend fun deleteAllSynced(userId: Int)
}
