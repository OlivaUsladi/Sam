package com.example.myapplication.Finance.ui.transaction

import com.example.domain.Finance.model.Source
import com.example.domain.Finance.model.SourceType
import com.example.domain.Finance.model.Tag
import com.example.domain.Finance.model.Transaction
import com.example.domain.Finance.model.TransactionType
import com.example.domain.Finance.use_case.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionEditViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val getTransactions: GetTransactionsUseCase = mock()
    private val getSources: GetSourcesUseCase = mock()
    private val getTags: GetTagsUseCase = mock()
    private val createTx: CreateTransactionUseCase = mock()
    private val updateTx: UpdateTransactionUseCase = mock()
    private val createSource: CreateSourceUseCase = mock()
    private val createTag: CreateTagUseCase = mock()

    private lateinit var vm: TransactionEditViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        vm = TransactionEditViewModel(
                getTransactions, getSources, getTags,
                createTx, updateTx, createSource, createTag)
    }

    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `init with null id loads empty form`() = runTest(dispatcher) {
        whenever(getSources.invoke()).thenReturn(listOf(Source(1, "Карта", SourceType.CARD)))
        whenever(getTags.invoke()).thenReturn(listOf(Tag(1, "Еда", BigDecimal(0))))

        vm.onEvent(TransactionEditEvent.Init(null))
        advanceUntilIdle()

        val s = vm.state.value
        assertEquals("", s.name)
        assertEquals(1, s.sourceId)
        assertEquals(TransactionType.EXPENSE, s.type)
    }

    @Test
    fun `set amount filters non-numeric chars`() = runTest(dispatcher) {
        vm.onEvent(TransactionEditEvent.SetAmount("abc123.5xyz,5"))
        advanceUntilIdle()
        assertEquals("123.5,5", vm.state.value.amount)
    }

    @Test
    fun `setting income type drops tagId`() = runTest(dispatcher) {
        vm.onEvent(TransactionEditEvent.SetTag(5))
        vm.onEvent(TransactionEditEvent.SetType(TransactionType.INCOME))
        advanceUntilIdle()
        assertEquals(TransactionType.INCOME, vm.state.value.type)
        assertNull(vm.state.value.tagId)
    }

    @Test
    fun `save with blank name shows error`() = runTest(dispatcher) {
        vm.onEvent(TransactionEditEvent.SetAmount("100"))
        vm.onEvent(TransactionEditEvent.SetSource(1))
        vm.onEvent(TransactionEditEvent.Save)
        advanceUntilIdle()

        assertEquals("Заполните название, сумму и источник", vm.state.value.error)
    }

    @Test
    fun `save with zero amount shows error`() = runTest(dispatcher) {
        vm.onEvent(TransactionEditEvent.SetName("X"))
        vm.onEvent(TransactionEditEvent.SetAmount("0"))
        vm.onEvent(TransactionEditEvent.SetSource(1))
        vm.onEvent(TransactionEditEvent.Save)
        advanceUntilIdle()

        assertNotNull(vm.state.value.error)
    }

    @Test
    fun `save without source shows error`() = runTest(dispatcher) {
        vm.onEvent(TransactionEditEvent.SetName("X"))
        vm.onEvent(TransactionEditEvent.SetAmount("100"))
        vm.onEvent(TransactionEditEvent.Save)
        advanceUntilIdle()

        assertNotNull(vm.state.value.error)
    }

    @Test
    fun `save success calls createTx with parsed amount`() = runTest(dispatcher) {
        val mockTx = mock<Transaction>()
        whenever(createTx.invoke(any(), any(), any(), anyOrNull(), any(), any(), anyOrNull()))
                .thenReturn(mockTx)

        vm.onEvent(TransactionEditEvent.SetName("Кофе"))
        vm.onEvent(TransactionEditEvent.SetAmount("100,50"))
        vm.onEvent(TransactionEditEvent.SetSource(1))
        vm.onEvent(TransactionEditEvent.Save)
        advanceUntilIdle()

        verify(createTx).invoke(
                eq("Кофе"), eq(BigDecimal("100.50")), eq(TransactionType.EXPENSE),
                isNull(), any(), eq(1), isNull())
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun `save in income mode never passes tagId`() = runTest(dispatcher) {
        val mockTx = mock<Transaction>()
        whenever(createTx.invoke(any(), any(), any(), anyOrNull(), any(), any(), anyOrNull()))
                .thenReturn(mockTx)

        vm.onEvent(TransactionEditEvent.SetName("Зарплата"))
        vm.onEvent(TransactionEditEvent.SetAmount("50000"))
        vm.onEvent(TransactionEditEvent.SetSource(1))
        vm.onEvent(TransactionEditEvent.SetType(TransactionType.INCOME))
        vm.onEvent(TransactionEditEvent.SetTag(5)) // should be dropped on type change
        vm.onEvent(TransactionEditEvent.SetType(TransactionType.INCOME))
        vm.onEvent(TransactionEditEvent.Save)
        advanceUntilIdle()

        verify(createTx).invoke(
                eq("Зарплата"), eq(BigDecimal("50000")), eq(TransactionType.INCOME),
                isNull(), any(), eq(1), isNull())
    }

    @Test
    fun `save with existing id calls updateTx`() = runTest(dispatcher) {
        val tx = Transaction(
                id = 10, name = "Кофе", amount = BigDecimal("100"),
                type = TransactionType.EXPENSE, description = null,
                date = LocalDate.now(), sourceId = 1, tagId = null)
        whenever(getSources.invoke()).thenReturn(listOf(Source(1, "Карта", SourceType.CARD)))
        whenever(getTags.invoke()).thenReturn(listOf())
        whenever(getTransactions.invoke(null)).thenReturn(listOf(tx))
        val mockTx = mock<Transaction>()
        whenever(updateTx.invoke(any(), any(), any(), any(), anyOrNull(), any(), any(), anyOrNull()))
                .thenReturn(mockTx)

        vm.onEvent(TransactionEditEvent.Init(10))
        advanceUntilIdle()
        vm.onEvent(TransactionEditEvent.SetAmount("200"))
        vm.onEvent(TransactionEditEvent.Save)
        advanceUntilIdle()

        verify(updateTx).invoke(
                eq(10), eq("Кофе"), eq(BigDecimal("200")), eq(TransactionType.EXPENSE),
                isNull(), any(), eq(1), isNull())
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun `save failure exposes error`() = runTest(dispatcher) {
        whenever(createTx.invoke(any(), any(), any(), anyOrNull(), any(), any(), anyOrNull()))
                .thenThrow(RuntimeException("Server down"))

        vm.onEvent(TransactionEditEvent.SetName("X"))
        vm.onEvent(TransactionEditEvent.SetAmount("100"))
        vm.onEvent(TransactionEditEvent.SetSource(1))
        vm.onEvent(TransactionEditEvent.Save)
        advanceUntilIdle()

        assertEquals("Server down", vm.state.value.error)
        assertFalse(vm.state.value.saved)
    }
}
