package com.example.myapplication.Finance.ui.tagsgoals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.Goal
import com.example.domain.Finance.model.Tag
import com.example.domain.Finance.use_case.CalculateExpectedIncomeUseCase
import com.example.domain.Finance.use_case.DeleteGoalUseCase
import com.example.domain.Finance.use_case.DeleteTagUseCase
import com.example.domain.Finance.use_case.GetGoalsUseCase
import com.example.domain.Finance.use_case.GetTagsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class TagsGoalsUiState(
    val tags: List<Tag> = emptyList(),
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    // калькулятор
    val calcSum: String = "",
    val calcMonths: String = "",
    val calcPercent: String = "",
    val calcResult: BigDecimal? = null,
)

sealed class TagsGoalsEvent {
    data object Reload : TagsGoalsEvent()
    data class DeleteTag(val id: Int) : TagsGoalsEvent()
    data class DeleteGoal(val id: Int) : TagsGoalsEvent()
    data class CalcSum(val v: String) : TagsGoalsEvent()
    data class CalcMonths(val v: String) : TagsGoalsEvent()
    data class CalcPercent(val v: String) : TagsGoalsEvent()
    data object Calc : TagsGoalsEvent()
}

class TagsGoalsViewModel(
    private val getTags: GetTagsUseCase,
    private val getGoals: GetGoalsUseCase,
    private val deleteTag: DeleteTagUseCase,
    private val deleteGoal: DeleteGoalUseCase,
) : ViewModel() {

    private val calculator = CalculateExpectedIncomeUseCase()

    private val _state = MutableStateFlow(TagsGoalsUiState())
    val state: StateFlow<TagsGoalsUiState> = _state.asStateFlow()

    init { load() }

    fun onEvent(e: TagsGoalsEvent) {
        when (e) {
            TagsGoalsEvent.Reload      -> load()
            is TagsGoalsEvent.DeleteTag  -> removeTag(e.id)
            is TagsGoalsEvent.DeleteGoal -> removeGoal(e.id)
            is TagsGoalsEvent.CalcSum    -> _state.update { it.copy(calcSum = filterNum(e.v)) }
            is TagsGoalsEvent.CalcMonths -> _state.update { it.copy(calcMonths = e.v.filter { c -> c.isDigit() }) }
            is TagsGoalsEvent.CalcPercent-> _state.update { it.copy(calcPercent = filterNum(e.v)) }
            TagsGoalsEvent.Calc          -> calc()
        }
    }

    private fun filterNum(v: String) = v.filter { it.isDigit() || it == '.' || it == ',' }

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val tags = getTags(); val goals = getGoals()
            _state.update { it.copy(tags = tags, goals = goals, isLoading = false) }
        } catch (t: Throwable) {
            _state.update { it.copy(isLoading = false, error = t.message ?: "Ошибка") }
        }
    }

    private fun removeTag(id: Int) = viewModelScope.launch {
        try { deleteTag(id); load() }
        catch (t: Throwable) { _state.update { it.copy(error = t.message ?: "Не удалось удалить тэг") } }
    }

    private fun removeGoal(id: Int) = viewModelScope.launch {
        try { deleteGoal(id); load() }
        catch (t: Throwable) { _state.update { it.copy(error = t.message ?: "Не удалось удалить цель") } }
    }

    private fun calc() {
        val s = _state.value
        val sum     = s.calcSum.replace(',', '.').toBigDecimalOrNull() ?: BigDecimal.ZERO
        val months  = s.calcMonths.toIntOrNull() ?: 0
        val percent = s.calcPercent.replace(',', '.').toBigDecimalOrNull() ?: BigDecimal.ZERO
        _state.update { it.copy(calcResult = calculator(sum, months, percent)) }
    }
}
