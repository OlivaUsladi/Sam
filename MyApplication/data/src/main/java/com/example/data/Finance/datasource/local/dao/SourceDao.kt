package com.example.data.Finance.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.Finance.datasource.local.model.SourceLocalEntity

@Dao
interface SourceDao {

    @Query("SELECT * FROM fin_sources WHERE userId = :userId AND pendingDelete = 0 ORDER BY name ASC")
    suspend fun getAll(userId: Int): List<SourceLocalEntity>

    @Query("SELECT * FROM fin_sources WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): SourceLocalEntity?

    @Query("SELECT * FROM fin_sources WHERE userId = :userId AND name = :name LIMIT 1")
    suspend fun findByName(userId: Int, name: String): SourceLocalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SourceLocalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<SourceLocalEntity>)

    @Query("DELETE FROM fin_sources WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM fin_sources WHERE pendingCreate = 1 AND userId = :userId")
    suspend fun pendingCreate(userId: Int): List<SourceLocalEntity>

    @Query("SELECT * FROM fin_sources WHERE pendingUpdate = 1 AND userId = :userId")
    suspend fun pendingUpdate(userId: Int): List<SourceLocalEntity>

    @Query("SELECT * FROM fin_sources WHERE pendingDelete = 1 AND userId = :userId")
    suspend fun pendingDelete(userId: Int): List<SourceLocalEntity>

    @Query("""
        DELETE FROM fin_sources
        WHERE userId = :userId
          AND pendingCreate = 0
          AND pendingUpdate = 0
          AND pendingDelete = 0
    """)
    suspend fun deleteAllSynced(userId: Int)
}
