package com.example.myapplication.Finance.ui.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.Transaction
import com.example.domain.Finance.model.TransactionType
import com.example.domain.Finance.use_case.AssignTagToTransactionsUseCase
import com.example.domain.Finance.use_case.GetTagsUseCase
import com.example.domain.Finance.use_case.GetTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AssignTagUiState(
    val tagId: Int = 0,
    val tagName: String = "",
    val expenses: List<Transaction> = emptyList(),
    val selected: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

sealed class AssignTagEvent {
    data class Init(val tagId: Int) : AssignTagEvent()
    data class Toggle(val txId: Int) : AssignTagEvent()
    data object Save : AssignTagEvent()
}

class AssignTagTransactionsViewModel(
    private val getTransactions: GetTransactionsUseCase,
    private val getTags: GetTagsUseCase,
    private val assignTagToTx: AssignTagToTransactionsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AssignTagUiState())
    val state: StateFlow<AssignTagUiState> = _state.asStateFlow()

    fun onEvent(e: AssignTagEvent) {
        when (e) {
            is AssignTagEvent.Init   -> load(e.tagId)
            is AssignTagEvent.Toggle -> _state.update {
                val s = it.selected.toMutableSet()
                if (!s.add(e.txId)) s.remove(e.txId)
                it.copy(selected = s)
            }
            AssignTagEvent.Save      -> save()
        }
    }

    private fun load(tagId: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null, tagId = tagId) }
        try {
            val expenses = getTransactions(TransactionType.EXPENSE)
            val name = getTags().firstOrNull { it.id == tagId }?.name.orEmpty()
            val initial = expenses.filter { it.tagId == tagId }.map { it.id }.toSet()
            _state.update {
                it.copy(
                    tagName = name,
                    expenses = expenses,
                    selected = initial,
                    isLoading = false,
                )
            }
        } catch (t: Throwable) {
            _state.update { it.copy(isLoading = false, error = t.message ?: "Ошибка") }
        }
    }

    private fun save() = viewModelScope.launch {
        val s = _state.value
        try {
            assignTagToTx(s.tagId, s.selected.toList())
            _state.update { it.copy(saved = true) }
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message ?: "Не удалось сохранить") }
        }
    }
}
