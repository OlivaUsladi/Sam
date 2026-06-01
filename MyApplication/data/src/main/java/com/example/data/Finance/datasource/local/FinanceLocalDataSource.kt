package com.example.data.Finance.datasource.local

import com.example.data.Finance.datasource.local.model.GoalLocalEntity
import com.example.data.Finance.datasource.local.model.SourceLocalEntity
import com.example.data.Finance.datasource.local.model.TagLocalEntity
import com.example.data.Finance.datasource.local.model.TransactionLocalEntity

interface FinanceLocalDataSource {

    suspend fun getSources(userId: Int): List<SourceLocalEntity>
    suspend fun findSource(id: Int): SourceLocalEntity?
    suspend fun findSourceByName(userId: Int, name: String): SourceLocalEntity?
    suspend fun upsertSource(entity: SourceLocalEntity)
    suspend fun upsertSources(entities: List<SourceLocalEntity>)
    suspend fun deleteSource(id: Int)
    suspend fun pendingCreateSources(userId: Int): List<SourceLocalEntity>
    suspend fun pendingUpdateSources(userId: Int): List<SourceLocalEntity>
    suspend fun pendingDeleteSources(userId: Int): List<SourceLocalEntity>
    suspend fun replaceAllSyncedSources(userId: Int, newRows: List<SourceLocalEntity>)

    suspend fun getTags(userId: Int): List<TagLocalEntity>
    suspend fun findTag(id: Int): TagLocalEntity?
    suspend fun findTagByName(userId: Int, name: String): TagLocalEntity?
    suspend fun upsertTag(entity: TagLocalEntity)
    suspend fun upsertTags(entities: List<TagLocalEntity>)
    suspend fun deleteTag(id: Int)
    suspend fun pendingCreateTags(userId: Int): List<TagLocalEntity>
    suspend fun pendingUpdateTags(userId: Int): List<TagLocalEntity>
    suspend fun pendingDeleteTags(userId: Int): List<TagLocalEntity>
    suspend fun replaceAllSyncedTags(userId: Int, newRows: List<TagLocalEntity>)

    suspend fun getGoals(userId: Int): List<GoalLocalEntity>
    suspend fun findGoal(id: Int): GoalLocalEntity?
    suspend fun upsertGoal(entity: GoalLocalEntity)
    suspend fun upsertGoals(entities: List<GoalLocalEntity>)
    suspend fun deleteGoal(id: Int)
    suspend fun pendingCreateGoals(userId: Int): List<GoalLocalEntity>
    suspend fun pendingUpdateGoals(userId: Int): List<GoalLocalEntity>
    suspend fun pendingDeleteGoals(userId: Int): List<GoalLocalEntity>
    suspend fun replaceAllSyncedGoals(userId: Int, newRows: List<GoalLocalEntity>)

    suspend fun getTransactions(
        userId: Int, type: String?, from: String?, to: String?,
    ): List<TransactionLocalEntity>

    suspend fun getTransactionsByTag(userId: Int, tagId: Int): List<TransactionLocalEntity>
    suspend fun findTransaction(id: Int): TransactionLocalEntity?
    suspend fun upsertTransaction(entity: TransactionLocalEntity)
    suspend fun upsertTransactions(entities: List<TransactionLocalEntity>)
    suspend fun deleteTransaction(id: Int)
    suspend fun pendingCreateTransactions(userId: Int): List<TransactionLocalEntity>
    suspend fun pendingUpdateTransactions(userId: Int): List<TransactionLocalEntity>
    suspend fun pendingDeleteTransactions(userId: Int): List<TransactionLocalEntity>
    suspend fun replaceAllSyncedTransactions(userId: Int, newRows: List<TransactionLocalEntity>)

    suspend fun listForAnalytics(
        userId: Int, type: String, from: String, to: String,
    ): List<TransactionLocalEntity>

    suspend fun findDuplicateTransactions(
        userId: Int,
        sourceId: Int,
        type: String,
        transactionDate: String,
        name: String,
        amount: String,
    ): List<TransactionLocalEntity>
}
