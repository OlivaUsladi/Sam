package com.example.data.Finance.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.Finance.datasource.local.model.TagLocalEntity

@Dao
interface TagDao {

    @Query("SELECT * FROM fin_tags WHERE userId = :userId AND pendingDelete = 0 ORDER BY name ASC")
    suspend fun getAll(userId: Int): List<TagLocalEntity>

    @Query("SELECT * FROM fin_tags WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): TagLocalEntity?

    @Query("SELECT * FROM fin_tags WHERE userId = :userId AND name = :name LIMIT 1")
    suspend fun findByName(userId: Int, name: String): TagLocalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TagLocalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<TagLocalEntity>)

    @Query("DELETE FROM fin_tags WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM fin_tags WHERE pendingCreate = 1 AND userId = :userId")
    suspend fun pendingCreate(userId: Int): List<TagLocalEntity>

    @Query("SELECT * FROM fin_tags WHERE pendingUpdate = 1 AND userId = :userId")
    suspend fun pendingUpdate(userId: Int): List<TagLocalEntity>

    @Query("SELECT * FROM fin_tags WHERE pendingDelete = 1 AND userId = :userId")
    suspend fun pendingDelete(userId: Int): List<TagLocalEntity>

    @Query("""
        DELETE FROM fin_tags
        WHERE userId = :userId
          AND pendingCreate = 0
          AND pendingUpdate = 0
          AND pendingDelete = 0
    """)
    suspend fun deleteAllSynced(userId: Int)
}
