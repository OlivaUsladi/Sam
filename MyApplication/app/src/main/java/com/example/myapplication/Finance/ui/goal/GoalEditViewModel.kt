package com.example.myapplication.Finance.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.use_case.CreateGoalUseCase
import com.example.domain.Finance.use_case.GetGoalUseCase
import com.example.domain.Finance.use_case.UpdateGoalUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate

data class GoalEditUiState(
    val id: Int? = null,
    val name: String = "",
    val description: String = "",
    val targetAmount: String = "",
    val currentAmount: String = "",
    val targetDate: LocalDate? = null,
    val monthlyAmount: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

sealed class GoalEditEvent {
    data class Init(val id: Int?) : GoalEditEvent()
    data class SetName(val v: String) : GoalEditEvent()
    data class SetDescription(val v: String) : GoalEditEvent()
    data class SetTarget(val v: String) : GoalEditEvent()
    data class SetCurrent(val v: String) : GoalEditEvent()
    data class SetDate(val v: LocalDate?) : GoalEditEvent()
    data class SetMonthly(val v: String) : GoalEditEvent()
    data object Save : GoalEditEvent()
}

class GoalEditViewModel(
    private val getGoal: GetGoalUseCase,
    private val createGoal: CreateGoalUseCase,
    private val updateGoal: UpdateGoalUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(GoalEditUiState())
    val state: StateFlow<GoalEditUiState> = _state.asStateFlow()

    fun onEvent(e: GoalEditEvent) {
        when (e) {
            is GoalEditEvent.Init           -> load(e.id)
            is GoalEditEvent.SetName        -> _state.update { it.copy(name = e.v) }
            is GoalEditEvent.SetDescription -> _state.update { it.copy(description = e.v) }
            is GoalEditEvent.SetTarget      -> _state.update { it.copy(targetAmount = filterNum(e.v)) }
            is GoalEditEvent.SetCurrent     -> _state.update { it.copy(currentAmount = filterNum(e.v)) }
            is GoalEditEvent.SetDate        -> _state.update { it.copy(targetDate = e.v) }
            is GoalEditEvent.SetMonthly     -> _state.update { it.copy(monthlyAmount = filterNum(e.v)) }
            GoalEditEvent.Save              -> save()
        }
    }

    private fun filterNum(v: String) = v.filter { it.isDigit() || it == '.' || it == ',' }

    private fun load(id: Int?) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            if (id == null) {
                _state.update { GoalEditUiState(isLoading = false) }
            } else {
                val g = getGoal(id)
                _state.update {
                    it.copy(
                        id = g.id,
                        name = g.name,
                        description = g.description.orEmpty(),
                        targetAmount = g.targetAmount.toPlainString(),
                        currentAmount = g.currentAmount.toPlainString(),
                        targetDate = g.targetDate,
                        monthlyAmount = g.monthlyAmount?.toPlainString().orEmpty(),
                        isLoading = false,
                    )
                }
            }
        } catch (t: Throwable) {
            _state.update { it.copy(isLoading = false, error = t.message ?: "Ошибка") }
        }
    }

    private fun save() = viewModelScope.launch {
        val s = _state.value
        val target = s.targetAmount.replace(',', '.').toBigDecimalOrNull()
        if (s.name.isBlank() || target == null || target.signum() <= 0) {
            _state.update { it.copy(error = "Заполните название и целевую сумму") }
            return@launch
        }
        val current = s.currentAmount.replace(',', '.').toBigDecimalOrNull() ?: BigDecimal.ZERO
        val monthly = s.monthlyAmount.replace(',', '.').toBigDecimalOrNull()
        try {
            if (s.id == null) {
                createGoal(s.name.trim(), s.description.ifBlank { null }, target, s.targetDate, monthly)
            } else {
                updateGoal(s.id, s.name.trim(), s.description.ifBlank { null },
                    target, current, s.targetDate, monthly)
            }
            _state.update { it.copy(saved = true, error = null) }
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message ?: "Не удалось сохранить") }
        }
    }
}
