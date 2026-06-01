package com.example.data.Finance.repository

import com.example.data.Auth.datasource.local.TokenStorage
import com.example.data.Finance.datasource.local.FinanceLocalDataSource
import com.example.data.Finance.datasource.local.model.GoalLocalEntity
import com.example.data.Finance.datasource.local.model.SourceLocalEntity
import com.example.data.Finance.datasource.local.model.TagLocalEntity
import com.example.data.Finance.datasource.local.model.TransactionLocalEntity
import com.example.data.Finance.datasource.remote.FinanceRemoteDataSource
import com.example.data.Finance.datasource.remote.dto.*
import com.example.data.Finance.datasource.remote.mapper.FinanceMappers.toDomain
import com.example.data.common.network.NetworkMonitor
import com.example.domain.Finance.model.*
import com.example.domain.Finance.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class FinanceRepositoryImpl(
    private val remote: FinanceRemoteDataSource,
    private val local: FinanceLocalDataSource,
    private val tokenStorage: TokenStorage,
    private val networkMonitor: NetworkMonitor,
) : FinanceRepository {

    private val isoDate: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val ymFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    private val _dataVersion = MutableStateFlow(0L)
    private val versionCounter = AtomicLong(0L)
    private fun bump() { _dataVersion.value = versionCounter.incrementAndGet() }
    override fun observeDataVersion(): Flow<Long> = _dataVersion.asStateFlow()

    private val _offline = MutableStateFlow(false)
    override fun observeOfflineMode(): Flow<Boolean> = _offline.asStateFlow()

    private val _analyticsTarget = MutableStateFlow<YearMonth?>(null)
    override fun observeAnalyticsTarget(): Flow<YearMonth?> = _analyticsTarget.asStateFlow()
    override suspend fun consumeAnalyticsTarget() { _analyticsTarget.value = null }

    private val localIdCounter = AtomicInteger(-1)
    private fun nextLocalId(): Int = localIdCounter.getAndDecrement()

    private fun userId(): Int = tokenStorage.getUserId() ?: 0

    private fun markOffline() { _offline.value = true }
    private fun markOnline()  { _offline.value = false }

    private suspend inline fun <T> tryRemote(remote: () -> T): Result<T> {
        if (!networkMonitor.isOnline.value) {
            markOffline()
            return Result.failure(IOException("Offline: network unavailable"))
        }
        return try {
            val v = remote()
            markOnline()
            Result.success(v)
        } catch (t: Throwable) {
            if (!networkMonitor.isOnline.value) markOffline()
            Result.failure(t)
        }
    }

    private fun SourceLocalEntity.toDomain() = Source(id, name, SourceType.fromRaw(type))
    private fun TagLocalEntity.toDomain() = Tag(id, name, BigDecimal(totalAmountSpent))
    private fun GoalLocalEntity.toDomain() = Goal(
        id = id,
        name = name,
        description = description,
        targetAmount = BigDecimal(targetAmount),
        currentAmount = BigDecimal(currentAmount),
        targetDate = targetDate?.let(LocalDate::parse),
        monthlyAmount = monthlyAmount?.let(::BigDecimal),
    )
    private fun TransactionLocalEntity.toDomain() = Transaction(
        id = id,
        name = name,
        amount = BigDecimal(amount),
        type = TransactionType.fromRaw(type),
        description = description,
        date = LocalDate.parse(transactionDate),
        sourceId = sourceId,
        tagId = tagId,
    )

    private fun Source.toLocal(uid: Int) = SourceLocalEntity(id, uid, name, type.raw)
    private fun Tag.toLocal(uid: Int) = TagLocalEntity(id, uid, name, totalAmountSpent.toPlainString())
    private fun Goal.toLocal(uid: Int) = GoalLocalEntity(
        id = id,
        userId = uid,
        name = name,
        description = description,
        targetAmount = targetAmount.toPlainString(),
        currentAmount = currentAmount.toPlainString(),
        targetDate = targetDate?.format(isoDate),
        monthlyAmount = monthlyAmount?.toPlainString(),
    )
    private fun Transaction.toLocal(uid: Int) = TransactionLocalEntity(
        id = id,
        userId = uid,
        name = name,
        amount = amount.toPlainString(),
        type = type.raw,
        description = description,
        transactionDate = date.format(isoDate),
        sourceId = sourceId,
        tagId = tagId,
    )

    override suspend fun getSources(): List<Source> {
        val uid = userId()
        tryRemote { remote.listSources().map { it.toDomain() } }
            .onSuccess { fresh ->
                local.replaceAllSyncedSources(uid, fresh.map { it.toLocal(uid) })
            }
        return local.getSources(uid).map { it.toDomain() }
    }

    override suspend fun createSource(name: String, type: SourceType): Source {
        val uid = userId()
        val localId = nextLocalId()
        local.upsertSource(SourceLocalEntity(
            id = localId, userId = uid, name = name, type = type.raw, pendingCreate = true,
        ))
        bump()
        tryRemote { remote.createSource(name, type.raw).toDomain() }
            .onSuccess { serverSource ->
                local.deleteSource(localId)
                local.upsertSource(serverSource.toLocal(uid))
                bump()
            }
        val saved = local.findSource(localId) ?: local.findSourceByName(uid, name)
        return saved?.toDomain() ?: Source(localId, name, type)
    }

    override suspend fun updateSource(id: Int, name: String, type: SourceType): Source {
        val uid = userId()
        val current = local.findSource(id)
        if (current != null) {
            local.upsertSource(current.copy(
                name = name, type = type.raw,
                pendingUpdate = !current.pendingCreate,
            ))
            bump()
        }
        tryRemote { remote.updateSource(id, name, type.raw).toDomain() }
            .onSuccess { upd ->
                local.upsertSource(upd.toLocal(uid))
                bump()
            }
        return local.findSource(id)?.toDomain() ?: Source(id, name, type)
    }

    override suspend fun deleteSource(id: Int) {
        val current = local.findSource(id)
        if (current != null) {
            if (current.pendingCreate) local.deleteSource(id)
            else local.upsertSource(current.copy(pendingDelete = true))
            bump()
        }
        tryRemote { remote.deleteSource(id); Unit }
            .onSuccess { local.deleteSource(id); bump() }
    }


    override suspend fun getTags(): List<Tag> {
        val uid = userId()
        tryRemote { remote.listTags().map { it.toDomain() } }
            .onSuccess { fresh ->
                local.replaceAllSyncedTags(uid, fresh.map { it.toLocal(uid) })
            }
        return local.getTags(uid).map { it.toDomain() }
    }

    override suspend fun createTag(name: String): Tag {
        val uid = userId()
        val localId = nextLocalId()
        local.upsertTag(TagLocalEntity(
            id = localId, userId = uid, name = name,
            totalAmountSpent = "0", pendingCreate = true,
        ))
        bump()
        tryRemote { remote.createTag(name).toDomain() }
            .onSuccess { upd ->
                local.deleteTag(localId)
                local.upsertTag(upd.toLocal(uid))
                bump()
            }
        val saved = local.findTag(localId) ?: local.findTagByName(uid, name)
        return saved?.toDomain() ?: Tag(localId, name, BigDecimal.ZERO)
    }

    override suspend fun updateTag(id: Int, name: String): Tag {
        val uid = userId()
        val current = local.findTag(id)
        if (current != null) {
            local.upsertTag(current.copy(
                name = name,
                pendingUpdate = !current.pendingCreate,
            ))
            bump()
        }
        tryRemote { remote.updateTag(id, name).toDomain() }
            .onSuccess { upd -> local.upsertTag(upd.toLocal(uid)); bump() }
        return local.findTag(id)?.toDomain()
            ?: Tag(id, name, current?.totalAmountSpent?.let(::BigDecimal) ?: BigDecimal.ZERO)
    }

    override suspend fun deleteTag(id: Int) {
        val current = local.findTag(id)
        if (current != null) {
            if (current.pendingCreate) local.deleteTag(id)
            else local.upsertTag(current.copy(pendingDelete = true))
            bump()
        }
        tryRemote { remote.deleteTag(id); Unit }
            .onSuccess { local.deleteTag(id); bump() }
    }

    override suspend fun getTransactions(
        type: TransactionType?, from: LocalDate?, to: LocalDate?,
    ): List<Transaction> {
        val uid = userId()
        val fromStr = from?.format(isoDate)
        val toStr   = to?.format(isoDate)

        tryRemote {
            remote.listTransactions(type?.raw, fromStr, toStr).map { it.toDomain() }
        }.onSuccess { fresh ->
            local.replaceAllSyncedTransactions(uid, fresh.map { it.toLocal(uid) })
            for (server in fresh) {
                val dups = local.findDuplicateTransactions(
                    userId = uid,
                    sourceId = server.sourceId,
                    type = server.type.raw,
                    transactionDate = server.date.format(isoDate),
                    name = server.name,
                    amount = server.amount.toPlainString(),
                )
                dups.filter { it.id != server.id && it.pendingCreate }
                    .forEach { local.deleteTransaction(it.id) }
            }
        }

        return local.getTransactions(uid, type?.raw, fromStr, toStr).map { it.toDomain() }
    }

    override suspend fun getTransactionsByTag(tagId: Int): List<Transaction> {
        val uid = userId()
        tryRemote { remote.listTagTransactions(tagId).map { it.toDomain() } }
            .onSuccess { fresh ->
                fresh.forEach { local.upsertTransaction(it.toLocal(uid)) }
            }
        return local.getTransactionsByTag(uid, tagId).map { it.toDomain() }
    }

    override suspend fun createTransaction(
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        description: String?,
        date: LocalDate,
        sourceId: Int,
        tagId: Int?,
    ): Transaction {
        val uid = userId()
        val localId = nextLocalId()
        val pending = TransactionLocalEntity(
            id = localId, userId = uid, name = name,
            amount = amount.toPlainString(), type = type.raw,
            description = description,
            transactionDate = date.format(isoDate),
            sourceId = sourceId, tagId = tagId,
            pendingCreate = true,
        )
        local.upsertTransaction(pending)
        bump()
        tryRemote {
            remote.createTransaction(
                CreateTransactionRequestDto(
                    name = name, amount = amount, type = type.raw,
                    description = description,
                    transactionDate = date.format(isoDate),
                    sourceId = sourceId, tagId = tagId,
                )
            ).toDomain()
        }.onSuccess { upd ->
            local.deleteTransaction(localId)
            local.upsertTransaction(upd.toLocal(uid))
            bump()
        }
        return local.findTransaction(localId)?.toDomain()
            ?: Transaction(localId, name, amount, type, description, date, sourceId, tagId)
    }

    override suspend fun updateTransaction(
        id: Int,
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        description: String?,
        date: LocalDate,
        sourceId: Int,
        tagId: Int?,
    ): Transaction {
        val uid = userId()
        val current = local.findTransaction(id)
        val updatedLocal = (current ?: TransactionLocalEntity(
            id = id, userId = uid, name = name,
            amount = amount.toPlainString(), type = type.raw,
            description = description, transactionDate = date.format(isoDate),
            sourceId = sourceId, tagId = tagId,
        )).copy(
            name = name, amount = amount.toPlainString(), type = type.raw,
            description = description, transactionDate = date.format(isoDate),
            sourceId = sourceId, tagId = tagId,
            pendingUpdate = current?.pendingCreate?.not() ?: false,
        )
        local.upsertTransaction(updatedLocal)
        bump()
        tryRemote {
            remote.updateTransaction(
                id,
                UpdateTransactionRequestDto(
                    name = name, amount = amount, type = type.raw,
                    description = description,
                    transactionDate = date.format(isoDate),
                    sourceId = sourceId, tagId = tagId,
                ),
            ).toDomain()
        }.onSuccess { upd -> local.upsertTransaction(upd.toLocal(uid)); bump() }
        return local.findTransaction(id)?.toDomain() ?: updatedLocal.toDomain()
    }

    override suspend fun deleteTransaction(id: Int) {
        val current = local.findTransaction(id)
        if (current != null) {
            if (current.pendingCreate) local.deleteTransaction(id)
            else local.upsertTransaction(current.copy(pendingDelete = true))
            bump()
        }
        tryRemote { remote.deleteTransaction(id); Unit }
            .onSuccess { local.deleteTransaction(id); bump() }
    }

    override suspend fun assignTagToTransactions(tagId: Int, transactionIds: List<Int>) {
        tryRemote { remote.assignTagToTransactions(tagId, transactionIds); Unit }
        bump()
    }


    override suspend fun getGoals(): List<Goal> {
        val uid = userId()
        tryRemote { remote.listGoals().map { it.toDomain() } }
            .onSuccess { fresh ->
                local.replaceAllSyncedGoals(uid, fresh.map { it.toLocal(uid) })
            }
        return local.getGoals(uid).map { it.toDomain() }
    }

    override suspend fun getGoal(id: Int): Goal {
        tryRemote { remote.getGoal(id).toDomain() }
            .onSuccess { fresh ->
                local.upsertGoal(fresh.toLocal(userId()))
            }
        return local.findGoal(id)?.toDomain()
            ?: throw IllegalStateException("Цель не найдена ни на сервере, ни локально")
    }

    override suspend fun createGoal(
        name: String, description: String?,
        targetAmount: BigDecimal, targetDate: LocalDate?, monthlyAmount: BigDecimal?,
    ): Goal {
        val uid = userId()
        val localId = nextLocalId()
        local.upsertGoal(GoalLocalEntity(
            id = localId, userId = uid, name = name, description = description,
            targetAmount = targetAmount.toPlainString(), currentAmount = "0",
            targetDate = targetDate?.format(isoDate),
            monthlyAmount = monthlyAmount?.toPlainString(),
            pendingCreate = true,
        ))
        bump()
        tryRemote {
            remote.createGoal(CreateGoalRequestDto(
                name = name, description = description,
                targetAmount = targetAmount,
                targetDate = targetDate?.format(isoDate),
                monthlyAmount = monthlyAmount,
            )).toDomain()
        }.onSuccess { upd ->
            local.deleteGoal(localId)
            local.upsertGoal(upd.toLocal(uid))
            bump()
        }
        return local.findGoal(localId)?.toDomain()
            ?: Goal(localId, name, description, targetAmount, BigDecimal.ZERO, targetDate, monthlyAmount)
    }

    override suspend fun updateGoal(
        id: Int, name: String, description: String?,
        targetAmount: BigDecimal, currentAmount: BigDecimal,
        targetDate: LocalDate?, monthlyAmount: BigDecimal?,
    ): Goal {
        val uid = userId()
        val current = local.findGoal(id)
        if (current != null) {
            local.upsertGoal(current.copy(
                name = name, description = description,
                targetAmount = targetAmount.toPlainString(),
                currentAmount = currentAmount.toPlainString(),
                targetDate = targetDate?.format(isoDate),
                monthlyAmount = monthlyAmount?.toPlainString(),
                pendingUpdate = !current.pendingCreate,
            ))
            bump()
        }
        tryRemote {
            remote.updateGoal(id, UpdateGoalRequestDto(
                name = name, description = description,
                targetAmount = targetAmount, currentAmount = currentAmount,
                targetDate = targetDate?.format(isoDate),
                monthlyAmount = monthlyAmount,
            )).toDomain()
        }.onSuccess { upd -> local.upsertGoal(upd.toLocal(uid)); bump() }
        return local.findGoal(id)?.toDomain()
            ?: Goal(id, name, description, targetAmount, currentAmount, targetDate, monthlyAmount)
    }

    override suspend fun deleteGoal(id: Int) {
        val current = local.findGoal(id)
        if (current != null) {
            if (current.pendingCreate) local.deleteGoal(id)
            else local.upsertGoal(current.copy(pendingDelete = true))
            bump()
        }
        tryRemote { remote.deleteGoal(id); Unit }
            .onSuccess { local.deleteGoal(id); bump() }
    }


    override suspend fun getAnalytics(month: YearMonth, type: TransactionType): Analytics {
        return tryRemote { remote.getAnalytics(month.format(ymFormat), type.raw).toDomain() }
            .getOrElse {
                val uid = userId()
                val from = month.atDay(1)
                val to   = month.atEndOfMonth()
                val rows = local.listForAnalytics(
                    userId = uid, type = type.raw,
                    from = from.format(isoDate), to = to.format(isoDate),
                )
                val total = rows.fold(BigDecimal.ZERO) { acc, r -> acc + BigDecimal(r.amount) }
                val daily = rows.groupBy { it.transactionDate }
                    .map { (d, list) ->
                        DailyTotal(
                            LocalDate.parse(d),
                            list.fold(BigDecimal.ZERO) { acc, r -> acc + BigDecimal(r.amount) },
                        )
                    }
                    .sortedBy { it.date }
                val sourcesById = local.getSources(uid).associateBy { it.id }
                val bySource = rows.groupBy { it.sourceId }.map { (sid, list) ->
                    SourceBucket(
                        sid,
                        sourcesById[sid]?.name ?: "—",
                        list.fold(BigDecimal.ZERO) { acc, r -> acc + BigDecimal(r.amount) },
                    )
                }
                val byTag = if (type == TransactionType.EXPENSE) {
                    val tagsById = local.getTags(uid).associateBy { it.id }
                    rows.filter { it.tagId != null }.groupBy { it.tagId!! }.map { (tid, list) ->
                        TagBucket(
                            tid,
                            tagsById[tid]?.name ?: "—",
                            list.fold(BigDecimal.ZERO) { acc, r -> acc + BigDecimal(r.amount) },
                        )
                    }
                } else null

                Analytics(
                    periodStart = from, periodEnd = to,
                    type = type, total = total, daily = daily,
                    bySource = bySource, byTag = byTag,
                )
            }
    }

    override suspend fun importBankReport(
        fileName: String, sourceId: Int, content: ByteArray,
    ): ImportReport {
        val report = remote.importBankReport(fileName, sourceId, content).toDomain()
        runCatching { getTransactions() }
        bump()
        report.suggestedMonth?.let { _analyticsTarget.value = it }
        return report
    }

    override suspend fun syncNow() {
        if (!networkMonitor.isOnline.value) return
        val uid = userId()

        val sourceIdMap = HashMap<Int, Int>()
        local.pendingCreateSources(uid).forEach { row ->
            runCatching {
                val server = remote.createSource(row.name, row.type).toDomain()
                local.deleteSource(row.id)
                local.upsertSource(server.toLocal(uid))
                sourceIdMap[row.id] = server.id
            }
        }
        local.pendingUpdateSources(uid).forEach { row ->
            runCatching {
                val server = remote.updateSource(row.id, row.name, row.type).toDomain()
                local.upsertSource(server.toLocal(uid))
            }
        }
        local.pendingDeleteSources(uid).forEach { row ->
            runCatching {
                remote.deleteSource(row.id)
                local.deleteSource(row.id)
            }
        }

        val tagIdMap = HashMap<Int, Int>()
        local.pendingCreateTags(uid).forEach { row ->
            runCatching {
                val server = remote.createTag(row.name).toDomain()
                local.deleteTag(row.id)
                local.upsertTag(server.toLocal(uid))
                tagIdMap[row.id] = server.id
            }
        }
        local.pendingUpdateTags(uid).forEach { row ->
            runCatching {
                val server = remote.updateTag(row.id, row.name).toDomain()
                local.upsertTag(server.toLocal(uid))
            }
        }
        local.pendingDeleteTags(uid).forEach { row ->
            runCatching {
                remote.deleteTag(row.id)
                local.deleteTag(row.id)
            }
        }

        local.pendingCreateGoals(uid).forEach { row ->
            runCatching {
                val server = remote.createGoal(CreateGoalRequestDto(
                    name = row.name, description = row.description,
                    targetAmount = BigDecimal(row.targetAmount),
                    targetDate = row.targetDate,
                    monthlyAmount = row.monthlyAmount?.let(::BigDecimal),
                )).toDomain()
                local.deleteGoal(row.id)
                local.upsertGoal(server.toLocal(uid))
            }
        }
        local.pendingUpdateGoals(uid).forEach { row ->
            runCatching {
                remote.updateGoal(row.id, UpdateGoalRequestDto(
                    name = row.name, description = row.description,
                    targetAmount = BigDecimal(row.targetAmount),
                    currentAmount = BigDecimal(row.currentAmount),
                    targetDate = row.targetDate,
                    monthlyAmount = row.monthlyAmount?.let(::BigDecimal),
                ))
                local.upsertGoal(row.copy(pendingUpdate = false))
            }
        }
        local.pendingDeleteGoals(uid).forEach { row ->
            runCatching {
                remote.deleteGoal(row.id)
                local.deleteGoal(row.id)
            }
        }

        local.pendingCreateTransactions(uid).forEach { row ->
            runCatching {
                val realSourceId = sourceIdMap[row.sourceId] ?: row.sourceId
                val realTagId = row.tagId?.let { tagIdMap[it] ?: it }
                val server = remote.createTransaction(CreateTransactionRequestDto(
                    name = row.name, amount = BigDecimal(row.amount),
                    type = row.type, description = row.description,
                    transactionDate = row.transactionDate,
                    sourceId = realSourceId, tagId = realTagId,
                )).toDomain()
                local.deleteTransaction(row.id)
                local.upsertTransaction(server.toLocal(uid))
            }
        }
        local.pendingUpdateTransactions(uid).forEach { row ->
            runCatching {
                val realSourceId = sourceIdMap[row.sourceId] ?: row.sourceId
                val realTagId = row.tagId?.let { tagIdMap[it] ?: it }
                remote.updateTransaction(row.id, UpdateTransactionRequestDto(
                    name = row.name, amount = BigDecimal(row.amount),
                    type = row.type, description = row.description,
                    transactionDate = row.transactionDate,
                    sourceId = realSourceId, tagId = realTagId,
                ))
                local.upsertTransaction(row.copy(
                    pendingUpdate = false,
                    sourceId = realSourceId, tagId = realTagId,
                ))
            }
        }
        local.pendingDeleteTransactions(uid).forEach { row ->
            runCatching {
                remote.deleteTransaction(row.id)
                local.deleteTransaction(row.id)
            }
        }

        runCatching { getSources() }
        runCatching { getTags() }
        runCatching { getGoals() }
        runCatching { getTransactions() }

        bump()
    }
}
