package com.example.data.Finance.datasource.local

import com.example.data.Finance.datasource.local.dao.GoalDao
import com.example.data.Finance.datasource.local.dao.SourceDao
import com.example.data.Finance.datasource.local.dao.TagDao
import com.example.data.Finance.datasource.local.dao.TransactionDao
import com.example.data.Finance.datasource.local.model.GoalLocalEntity
import com.example.data.Finance.datasource.local.model.SourceLocalEntity
import com.example.data.Finance.datasource.local.model.TagLocalEntity
import com.example.data.Finance.datasource.local.model.TransactionLocalEntity

class FinanceLocalDataSourceImpl(
    private val sourceDao: SourceDao,
    private val tagDao: TagDao,
    private val goalDao: GoalDao,
    private val transactionDao: TransactionDao,
) : FinanceLocalDataSource {

    override suspend fun getSources(userId: Int) = sourceDao.getAll(userId)
    override suspend fun findSource(id: Int) = sourceDao.findById(id)
    override suspend fun findSourceByName(userId: Int, name: String) =
        sourceDao.findByName(userId, name)
    override suspend fun upsertSource(entity: SourceLocalEntity) = sourceDao.upsert(entity)
    override suspend fun upsertSources(entities: List<SourceLocalEntity>) =
        sourceDao.upsertAll(entities)
    override suspend fun deleteSource(id: Int) = sourceDao.deleteById(id)
    override suspend fun pendingCreateSources(userId: Int) = sourceDao.pendingCreate(userId)
    override suspend fun pendingUpdateSources(userId: Int) = sourceDao.pendingUpdate(userId)
    override suspend fun pendingDeleteSources(userId: Int) = sourceDao.pendingDelete(userId)

    override suspend fun replaceAllSyncedSources(userId: Int, newRows: List<SourceLocalEntity>) {

        val pendingIds = (sourceDao.pendingUpdate(userId) + sourceDao.pendingDelete(userId))
            .map { it.id }.toHashSet()
        sourceDao.deleteAllSynced(userId)
        val filtered = newRows.filterNot { it.id in pendingIds }
        if (filtered.isNotEmpty()) sourceDao.upsertAll(filtered)
    }

    override suspend fun getTags(userId: Int) = tagDao.getAll(userId)
    override suspend fun findTag(id: Int) = tagDao.findById(id)
    override suspend fun findTagByName(userId: Int, name: String) =
        tagDao.findByName(userId, name)
    override suspend fun upsertTag(entity: TagLocalEntity) = tagDao.upsert(entity)
    override suspend fun upsertTags(entities: List<TagLocalEntity>) = tagDao.upsertAll(entities)
    override suspend fun deleteTag(id: Int) = tagDao.deleteById(id)
    override suspend fun pendingCreateTags(userId: Int) = tagDao.pendingCreate(userId)
    override suspend fun pendingUpdateTags(userId: Int) = tagDao.pendingUpdate(userId)
    override suspend fun pendingDeleteTags(userId: Int) = tagDao.pendingDelete(userId)

    override suspend fun replaceAllSyncedTags(userId: Int, newRows: List<TagLocalEntity>) {
        val pendingIds = (tagDao.pendingUpdate(userId) + tagDao.pendingDelete(userId))
            .map { it.id }.toHashSet()
        tagDao.deleteAllSynced(userId)
        val filtered = newRows.filterNot { it.id in pendingIds }
        if (filtered.isNotEmpty()) tagDao.upsertAll(filtered)
    }

    override suspend fun getGoals(userId: Int) = goalDao.getAll(userId)
    override suspend fun findGoal(id: Int) = goalDao.findById(id)
    override suspend fun upsertGoal(entity: GoalLocalEntity) = goalDao.upsert(entity)
    override suspend fun upsertGoals(entities: List<GoalLocalEntity>) = goalDao.upsertAll(entities)
    override suspend fun deleteGoal(id: Int) = goalDao.deleteById(id)
    override suspend fun pendingCreateGoals(userId: Int) = goalDao.pendingCreate(userId)
    override suspend fun pendingUpdateGoals(userId: Int) = goalDao.pendingUpdate(userId)
    override suspend fun pendingDeleteGoals(userId: Int) = goalDao.pendingDelete(userId)

    override suspend fun replaceAllSyncedGoals(userId: Int, newRows: List<GoalLocalEntity>) {
        val pendingIds = (goalDao.pendingUpdate(userId) + goalDao.pendingDelete(userId))
            .map { it.id }.toHashSet()
        goalDao.deleteAllSynced(userId)
        val filtered = newRows.filterNot { it.id in pendingIds }
        if (filtered.isNotEmpty()) goalDao.upsertAll(filtered)
    }

    override suspend fun getTransactions(
        userId: Int, type: String?, from: String?, to: String?,
    ) = transactionDao.list(userId, type, from, to)

    override suspend fun getTransactionsByTag(userId: Int, tagId: Int) =
        transactionDao.listByTag(userId, tagId)

    override suspend fun findTransaction(id: Int) = transactionDao.findById(id)
    override suspend fun upsertTransaction(entity: TransactionLocalEntity) =
        transactionDao.upsert(entity)
    override suspend fun upsertTransactions(entities: List<TransactionLocalEntity>) =
        transactionDao.upsertAll(entities)
    override suspend fun deleteTransaction(id: Int) = transactionDao.deleteById(id)
    override suspend fun pendingCreateTransactions(userId: Int) = transactionDao.pendingCreate(userId)
    override suspend fun pendingUpdateTransactions(userId: Int) = transactionDao.pendingUpdate(userId)
    override suspend fun pendingDeleteTransactions(userId: Int) = transactionDao.pendingDelete(userId)

    override suspend fun replaceAllSyncedTransactions(
        userId: Int, newRows: List<TransactionLocalEntity>,
    ) {
        val pendingIds = (transactionDao.pendingUpdate(userId) + transactionDao.pendingDelete(userId))
            .map { it.id }.toHashSet()
        transactionDao.deleteAllSynced(userId)
        val filtered = newRows.filterNot { it.id in pendingIds }
        if (filtered.isNotEmpty()) transactionDao.upsertAll(filtered)
    }

    override suspend fun listForAnalytics(
        userId: Int, type: String, from: String, to: String,
    ) = transactionDao.listForAnalytics(userId, type, from, to)

    override suspend fun findDuplicateTransactions(
        userId: Int, sourceId: Int, type: String,
        transactionDate: String, name: String, amount: String,
    ) = transactionDao.findDuplicates(
        userId, sourceId, type, transactionDate, name, amount,
    )
}
