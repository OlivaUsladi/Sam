package com.example.myapplication.Finance.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.Source
import com.example.domain.Finance.model.Tag
import com.example.domain.Finance.model.Transaction
import com.example.domain.Finance.model.TransactionType
import com.example.domain.Finance.use_case.DeleteTransactionUseCase
import com.example.domain.Finance.use_case.GetSourcesUseCase
import com.example.domain.Finance.use_case.GetTagsUseCase
import com.example.domain.Finance.use_case.GetTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HistoryFilter { ALL, INCOME, EXPENSE }

data class HistoryUiState(
    val filter: HistoryFilter = HistoryFilter.ALL,
    val sourceFilter: Int? = null,
    val transactions: List<Transaction> = emptyList(),
    val sources: List<Source> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class HistoryEvent {
    data object Reload : HistoryEvent()
    data class ChangeFilter(val f: HistoryFilter) : HistoryEvent()
    data class SetSourceFilter(val sourceId: Int?) : HistoryEvent()
    data class Delete(val id: Int) : HistoryEvent()
}

class HistoryViewModel(
    private val getTransactions: GetTransactionsUseCase,
    private val getSources: GetSourcesUseCase,
    private val getTags: GetTagsUseCase,
    private val deleteTx: DeleteTransactionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    init { onEvent(HistoryEvent.Reload) }

    fun onEvent(e: HistoryEvent) {
        when (e) {
            HistoryEvent.Reload      -> load()
            is HistoryEvent.ChangeFilter -> {
                _state.update { it.copy(filter = e.f) }
                load()
            }
            is HistoryEvent.SetSourceFilter -> {
                if (_state.value.sourceFilter != e.sourceId) {
                    _state.update { it.copy(sourceFilter = e.sourceId) }
                    load()
                }
            }
            is HistoryEvent.Delete   -> remove(e.id)
        }
    }

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val typeFilter = when (_state.value.filter) {
                HistoryFilter.ALL     -> null
                HistoryFilter.INCOME  -> TransactionType.INCOME
                HistoryFilter.EXPENSE -> TransactionType.EXPENSE
            }
            val srcFilter = _state.value.sourceFilter
            val allTxs = getTransactions(typeFilter)
            val txs = if (srcFilter == null) allTxs else allTxs.filter { it.sourceId == srcFilter }
            val srcs = getSources()
            val tags = getTags()
            _state.update { it.copy(transactions = txs, sources = srcs, tags = tags, isLoading = false) }
        } catch (t: Throwable) {
            _state.update { it.copy(isLoading = false, error = t.message ?: "Ошибка загрузки") }
        }
    }

    private fun remove(id: Int) = viewModelScope.launch {
        try {
            deleteTx(id)
            load()
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message ?: "Не удалось удалить") }
        }
    }
}
