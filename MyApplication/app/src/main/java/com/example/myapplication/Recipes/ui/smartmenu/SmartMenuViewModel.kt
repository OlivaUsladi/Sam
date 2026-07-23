package com.example.myapplication.Recipes.ui.smartmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ai.GigaChatService
import com.example.myapplication.ai.WeeklyMenu
import com.example.domain.Finance.use_case.GetTagsUseCase
import com.example.domain.Finance.use_case.GetTransactionsByTagUseCase
import com.example.domain.Finance.model.TransactionType
import com.example.domain.Recipes.model.ShoppingListItem
import com.example.domain.Recipes.use_case.CreateShoppingListUseCase
import com.example.domain.Recipes.use_case.AddItemToListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class SmartMenuUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val budgetInput: String = "",
    val autoDetectedBudget: BigDecimal? = null,
    val weeks: List<WeeklyMenu> = emptyList(),
    val addedWeeks: Set<Int> = emptySet(),
    val generated: Boolean = false
)

sealed class SmartMenuEvent {
    data object LoadBudget : SmartMenuEvent()
    data class SetBudget(val v: String) : SmartMenuEvent()
    data object Generate : SmartMenuEvent()
    data class AddShoppingList(val weekNumber: Int) : SmartMenuEvent()
}

class SmartMenuViewModel(
    private val getTagsUseCase: GetTagsUseCase,
    private val getTransactionsByTagUseCase: GetTransactionsByTagUseCase,
    private val createShoppingListUseCase: CreateShoppingListUseCase,
    private val addItemToListUseCase: AddItemToListUseCase,
    private val gigaChatService: GigaChatService
) : ViewModel() {

    private val _state = MutableStateFlow(SmartMenuUiState())
    val state: StateFlow<SmartMenuUiState> = _state.asStateFlow()

    fun onEvent(event: SmartMenuEvent) {
        when (event) {
            SmartMenuEvent.LoadBudget -> loadBudget()
            is SmartMenuEvent.SetBudget -> _state.update { it.copy(budgetInput = event.v) }
            SmartMenuEvent.Generate -> generate()
            is SmartMenuEvent.AddShoppingList -> addShoppingList(event.weekNumber)
        }
    }

    private fun loadBudget() = viewModelScope.launch {
        try {
            val tags = getTagsUseCase()
            val foodTag = tags.firstOrNull {
                it.name.equals("Продукты", ignoreCase = true) && it.monthlyLimit != null
            }
            if (foodTag != null) {
                val now = LocalDate.now()
                val txs = getTransactionsByTagUseCase(foodTag.id)
                val spentThisMonth = txs
                    .filter {
                        it.date.year == now.year && it.date.monthValue == now.monthValue &&
                                it.type == TransactionType.EXPENSE
                    }
                    .fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
                val remaining = (foodTag.monthlyLimit!! - spentThisMonth).max(BigDecimal.ZERO)
                _state.update {
                    it.copy(
                        autoDetectedBudget = remaining,
                        budgetInput = remaining.toPlainString()
                    )
                }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = e.message) }
        }
    }

    private fun generate() = viewModelScope.launch {
        val budget = _state.value.budgetInput.trim().replace(',', '.').toBigDecimalOrNull()
        if (budget == null || budget <= BigDecimal.ZERO) {
            _state.update { it.copy(error = "Введите корректный бюджет") }
            return@launch
        }

        _state.update { it.copy(isLoading = true, error = null, weeks = emptyList(), generated = false) }
        try {
            val now = LocalDate.now()
            val endOfMonth = now.withDayOfMonth(now.lengthOfMonth())
            val daysLeft = ChronoUnit.DAYS.between(now, endOfMonth).toInt() + 1
            val weeksCount = maxOf(1, (daysLeft + 6) / 7)

            val result = gigaChatService.generateMenu(budget, weeksCount)
            _state.update { it.copy(weeks = result, isLoading = false, generated = true) }
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, error = e.message ?: "Ошибка генерации") }
        }
    }

    private fun addShoppingList(weekNumber: Int) = viewModelScope.launch {
        val week = _state.value.weeks.firstOrNull { it.weekNumber == weekNumber } ?: return@launch
        try {
            val list = createShoppingListUseCase(
                userId = 1,
                name = "Меню: неделя $weekNumber"
            )
            for (item in week.shoppingItems) {
                addItemToListUseCase(
                    listId = list.id,
                    item = ShoppingListItem(
                        id = 0,
                        description = item.name,
                        isChecked = false,
                        quantity = item.quantity,
                        unit = item.unit
                    )
                )
            }
            _state.update { it.copy(addedWeeks = it.addedWeeks + weekNumber) }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Не удалось создать список: ${e.message}") }
        }
    }
}
