package com.example.data.Finance.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.Finance.datasource.local.model.TransactionLocalEntity

@Dao
interface TransactionDao {

    @Query("""
        SELECT * FROM fin_transactions
        WHERE userId = :userId
          AND pendingDelete = 0
          AND (:type IS NULL OR type = :type)
          AND (:from IS NULL OR transactionDate >= :from)
          AND (:to   IS NULL OR transactionDate <= :to)
        ORDER BY transactionDate DESC, id DESC
    """)
    suspend fun list(
        userId: Int,
        type: String?,
        from: String?,
        to: String?,
    ): List<TransactionLocalEntity>

    @Query("""
        SELECT * FROM fin_transactions
        WHERE userId = :userId AND tagId = :tagId AND pendingDelete = 0
        ORDER BY transactionDate DESC, id DESC
    """)
    suspend fun listByTag(userId: Int, tagId: Int): List<TransactionLocalEntity>

    @Query("SELECT * FROM fin_transactions WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): TransactionLocalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TransactionLocalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<TransactionLocalEntity>)

    @Query("DELETE FROM fin_transactions WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("""
        SELECT * FROM fin_transactions
        WHERE userId = :userId AND pendingDelete = 0
          AND type = :type
          AND transactionDate >= :from AND transactionDate <= :to
    """)
    suspend fun listForAnalytics(
        userId: Int,
        type: String,
        from: String,
        to: String,
    ): List<TransactionLocalEntity>

    @Query("SELECT * FROM fin_transactions WHERE pendingCreate = 1 AND userId = :userId")
    suspend fun pendingCreate(userId: Int): List<TransactionLocalEntity>

    @Query("SELECT * FROM fin_transactions WHERE pendingUpdate = 1 AND userId = :userId")
    suspend fun pendingUpdate(userId: Int): List<TransactionLocalEntity>

    @Query("SELECT * FROM fin_transactions WHERE pendingDelete = 1 AND userId = :userId")
    suspend fun pendingDelete(userId: Int): List<TransactionLocalEntity>

    @Query("""
        DELETE FROM fin_transactions
        WHERE userId = :userId
          AND pendingCreate = 0
          AND pendingDelete = 0
          AND pendingUpdate = 0
    """)
    suspend fun deleteAllSynced(userId: Int)

    @Query("""
        SELECT * FROM fin_transactions
        WHERE userId = :userId
          AND sourceId = :sourceId
          AND type = :type
          AND transactionDate = :transactionDate
          AND name = :name
          AND amount = :amount
    """)
    suspend fun findDuplicates(
        userId: Int,
        sourceId: Int,
        type: String,
        transactionDate: String,
        name: String,
        amount: String,
    ): List<TransactionLocalEntity>
}
