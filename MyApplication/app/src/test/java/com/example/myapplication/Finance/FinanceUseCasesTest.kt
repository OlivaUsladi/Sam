package com.example.domain.Finance.use_case

import com.example.domain.Finance.model.*
import com.example.domain.Finance.repository.FinanceRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDate

class FinanceUseCasesTest {

    private val repo: FinanceRepository = mock()

    @Test
    fun `CreateTransactionUseCase delegates with all params`() = runTest {
        val now = LocalDate.now()
        val tx = mock<Transaction>()
        whenever(repo.createTransaction("Кофе", BigDecimal("100"),
                TransactionType.EXPENSE, null, now, 1, 2)).thenReturn(tx)

        val result = CreateTransactionUseCase(repo)
                .invoke("Кофе", BigDecimal("100"), TransactionType.EXPENSE, null, now, 1, 2)

        assertEquals(tx, result)
    }

    @Test
    fun `CreateGoalUseCase delegates`() = runTest {
        val g = mock<Goal>()
        val targetDate = LocalDate.of(2026, 1, 1)
        whenever(repo.createGoal("Ноутбук", null, BigDecimal("120000"), targetDate, null))
                .thenReturn(g)

        val result = CreateGoalUseCase(repo)
                .invoke("Ноутбук", null, BigDecimal("120000"), targetDate, null)

        assertEquals(g, result)
    }

    @Test
    fun `CreateSourceUseCase delegates`() = runTest {
        val s = Source(1, "Карта", SourceType.CARD)
        whenever(repo.createSource("Карта", SourceType.CARD)).thenReturn(s)

        val result = CreateSourceUseCase(repo).invoke("Карта", SourceType.CARD)

        assertEquals(s, result)
    }

    @Test
    fun `CreateTagUseCase delegates`() = runTest {
        val t = Tag(
            1, "Еда", BigDecimal(0)
        )
        whenever(repo.createTag("Еда")).thenReturn(t)

        val result = CreateTagUseCase(repo).invoke("Еда")

        assertEquals(t, result)
    }

    @Test
    fun `DeleteGoalUseCase calls deleteGoal`() = runTest {
        DeleteGoalUseCase(repo).invoke(5)
        verify(repo).deleteGoal(5)
    }

    @Test
    fun `DeleteTransactionUseCase calls deleteTransaction`() = runTest {
        DeleteTransactionUseCase(repo).invoke(10)
        verify(repo).deleteTransaction(10)
    }

    @Test
    fun `DeleteSourceUseCase calls deleteSource`() = runTest {
        DeleteSourceUseCase(repo).invoke(3)
        verify(repo).deleteSource(3)
    }

    @Test
    fun `DeleteTagUseCase calls deleteTag`() = runTest {
        DeleteTagUseCase(repo).invoke(7)
        verify(repo).deleteTag(7)
    }

    @Test
    fun `AssignTagToTransactionsUseCase delegates`() = runTest {
        AssignTagToTransactionsUseCase(repo).invoke(1, listOf(10, 11))
        verify(repo).assignTagToTransactions(1, listOf(10, 11))
    }

    @Test
    fun `UpdateGoalUseCase delegates with currentAmount`() = runTest {
        val g = mock<Goal>()
        val td = LocalDate.now().plusMonths(6)
        whenever(repo.updateGoal(1, "X", null, BigDecimal("1000"),
                BigDecimal("500"), td, BigDecimal("100"))).thenReturn(g)

        val res = UpdateGoalUseCase(repo).invoke(
                1, "X", null, BigDecimal("1000"),
                BigDecimal("500"), td, BigDecimal("100"))

        assertEquals(g, res)
    }
}
