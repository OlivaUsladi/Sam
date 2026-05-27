package com.example.myapplication.Finance.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.Goal
import com.example.domain.Finance.use_case.DeleteGoalUseCase
import com.example.domain.Finance.use_case.GetGoalUseCase
import com.example.domain.Finance.use_case.ObserveFinanceChangesUseCase
import com.example.domain.Finance.use_case.UpdateGoalUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class GoalDetailUiState(
    val goal: Goal? = null,
    val addAmount: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val deleted: Boolean = false,
)

sealed class GoalDetailEvent {
    data class Init(val id: Int) : GoalDetailEvent()
    data class SetAdd(val v: String) : GoalDetailEvent()
    data object AddToCurrent : GoalDetailEvent()
    data object Delete : GoalDetailEvent()
}

class GoalDetailViewModel(
    private val getGoal: GetGoalUseCase,
    private val updateGoal: UpdateGoalUseCase,
    private val deleteGoal: DeleteGoalUseCase,
    private val observeChanges: ObserveFinanceChangesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(GoalDetailUiState())
    val state: StateFlow<GoalDetailUiState> = _state.asStateFlow()

    private var currentId: Int? = null

    init {
        viewModelScope.launch {
            observeChanges().collectLatest { currentId?.let { load(it) } }
        }
    }

    fun onEvent(e: GoalDetailEvent) {
        when (e) {
            is GoalDetailEvent.Init   -> { currentId = e.id; load(e.id) }
            is GoalDetailEvent.SetAdd -> _state.update {
                it.copy(addAmount = e.v.filter { c -> c.isDigit() || c == '.' || c == ',' })
            }
            GoalDetailEvent.AddToCurrent -> addToCurrent()
            GoalDetailEvent.Delete       -> remove()
        }
    }

    private fun load(id: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try { _state.update { it.copy(goal = getGoal(id), isLoading = false) } }
        catch (t: Throwable) {
            _state.update {
                it.copy(isLoading = false, goal = null,
                    error = t.message ?: "Цель не найдена")
            }
        }
    }

    private fun addToCurrent() = viewModelScope.launch {
        val s = _state.value
        val g = s.goal ?: return@launch
        val delta = s.addAmount.replace(',', '.').toBigDecimalOrNull() ?: return@launch
        if (delta.signum() <= 0) return@launch
        try {
            val newCurrent = (g.currentAmount + delta).min(g.targetAmount)
            val updated = updateGoal(g.id, g.name, g.description, g.targetAmount,
                newCurrent, g.targetDate, g.monthlyAmount)
            _state.update { it.copy(goal = updated, addAmount = "") }
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message ?: "Не удалось обновить цель") }
        }
    }

    private fun remove() = viewModelScope.launch {
        val g = _state.value.goal ?: return@launch
        try { deleteGoal(g.id); _state.update { it.copy(deleted = true) } }
        catch (t: Throwable) { _state.update { it.copy(error = t.message ?: "Не удалось удалить") } }
    }
}
