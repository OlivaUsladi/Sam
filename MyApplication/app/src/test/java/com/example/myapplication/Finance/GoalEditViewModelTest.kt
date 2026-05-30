package com.example.myapplication.Finance.ui.goal

import com.example.domain.Finance.model.Goal
import com.example.domain.Finance.use_case.CreateGoalUseCase
import com.example.domain.Finance.use_case.GetGoalUseCase
import com.example.domain.Finance.use_case.UpdateGoalUseCase
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
class GoalEditViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val getGoal: GetGoalUseCase = mock()
    private val createGoal: CreateGoalUseCase = mock()
    private val updateGoal: UpdateGoalUseCase = mock()

    private lateinit var vm: GoalEditViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        vm = GoalEditViewModel(getGoal, createGoal, updateGoal)
    }

    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `init with null id starts empty form`() = runTest(dispatcher) {
        vm.onEvent(GoalEditEvent.Init(null))
        advanceUntilIdle()
        assertNull(vm.state.value.id)
        assertEquals("", vm.state.value.name)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `init with id loads goal data`() = runTest(dispatcher) {
        val g = Goal(
                id = 1, name = "Ноутбук", description = "Apple",
                targetAmount = BigDecimal("120000"),
                currentAmount = BigDecimal("20000"),
                targetDate = LocalDate.of(2026, 1, 1),
                monthlyAmount = BigDecimal("10000"))
        whenever(getGoal.invoke(1)).thenReturn(g)

        vm.onEvent(GoalEditEvent.Init(1))
        advanceUntilIdle()

        val s = vm.state.value
        assertEquals(1, s.id)
        assertEquals("Ноутбук", s.name)
        assertEquals("120000", s.targetAmount)
        assertEquals("20000", s.currentAmount)
        assertEquals("10000", s.monthlyAmount)
    }

    @Test
    fun `set target filters non-numeric chars`() = runTest(dispatcher) {
        vm.onEvent(GoalEditEvent.SetTarget("abc1000.5xyz"))
        advanceUntilIdle()
        assertEquals("1000.5", vm.state.value.targetAmount)
    }

    @Test
    fun `save with blank name shows error`() = runTest(dispatcher) {
        vm.onEvent(GoalEditEvent.SetTarget("100"))
        vm.onEvent(GoalEditEvent.Save)
        advanceUntilIdle()

        assertEquals("Заполните название и целевую сумму", vm.state.value.error)
    }

    @Test
    fun `save with zero target shows error`() = runTest(dispatcher) {
        vm.onEvent(GoalEditEvent.SetName("X"))
        vm.onEvent(GoalEditEvent.SetTarget("0"))
        vm.onEvent(GoalEditEvent.Save)
        advanceUntilIdle()

        assertNotNull(vm.state.value.error)
    }

    @Test
    fun `save create calls createGoal with parsed values`() = runTest(dispatcher) {
        val mockGoal = mock<Goal>()
        whenever(createGoal.invoke(any(), anyOrNull(), any(), anyOrNull(), anyOrNull()))
                .thenReturn(mockGoal)

        vm.onEvent(GoalEditEvent.SetName("Ноутбук"))
        vm.onEvent(GoalEditEvent.SetTarget("120000,50"))
        vm.onEvent(GoalEditEvent.SetMonthly("10000"))
        vm.onEvent(GoalEditEvent.Save)
        advanceUntilIdle()

        verify(createGoal).invoke(
                eq("Ноутбук"), isNull(), eq(BigDecimal("120000.50")),
                isNull(), eq(BigDecimal("10000")))
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun `save update calls updateGoal`() = runTest(dispatcher) {
        val g = Goal(
                id = 5, name = "Old", description = null,
                targetAmount = BigDecimal("1000"),
                currentAmount = BigDecimal("100"),
                targetDate = null,
                monthlyAmount = null)
        whenever(getGoal.invoke(5)).thenReturn(g)
        whenever(updateGoal.invoke(any(), any(), anyOrNull(), any(), any(), anyOrNull(), anyOrNull()))
                .thenReturn(g)

        vm.onEvent(GoalEditEvent.Init(5))
        advanceUntilIdle()
        vm.onEvent(GoalEditEvent.SetName("New"))
        vm.onEvent(GoalEditEvent.Save)
        advanceUntilIdle()

        verify(updateGoal).invoke(
                eq(5), eq("New"), isNull(), eq(BigDecimal("1000")),
                eq(BigDecimal("100")), isNull(), isNull())
        assertTrue(vm.state.value.saved)
    }

    @Test
    fun `save failure shows error`() = runTest(dispatcher) {
        whenever(createGoal.invoke(any(), anyOrNull(), any(), anyOrNull(), anyOrNull()))
                .thenThrow(RuntimeException("Server down"))

        vm.onEvent(GoalEditEvent.SetName("X"))
        vm.onEvent(GoalEditEvent.SetTarget("1000"))
        vm.onEvent(GoalEditEvent.Save)
        advanceUntilIdle()

        assertEquals("Server down", vm.state.value.error)
        assertFalse(vm.state.value.saved)
    }
}
