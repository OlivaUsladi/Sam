package com.example.myapplication.Finance.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.Analytics
import com.example.domain.Finance.model.TransactionType
import com.example.domain.Finance.use_case.GetAnalyticsUseCase
import com.example.domain.Finance.use_case.ObserveFinanceChangesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth

data class AnalyticsUiState(
    val month: YearMonth = YearMonth.now(),
    val type: TransactionType = TransactionType.EXPENSE,
    val data: Analytics? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class AnalyticsEvent {
    data object Reload : AnalyticsEvent()
    data object PrevMonth : AnalyticsEvent()
    data object NextMonth : AnalyticsEvent()
    data class SetType(val v: TransactionType) : AnalyticsEvent()
}

class AnalyticsViewModel(
    private val getAnalytics: GetAnalyticsUseCase,
    private val observeChanges: ObserveFinanceChangesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsUiState())
    val state: StateFlow<AnalyticsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch { observeChanges().collectLatest { load() } }
    }

    fun onEvent(e: AnalyticsEvent) {
        when (e) {
            AnalyticsEvent.Reload    -> load()
            AnalyticsEvent.PrevMonth -> {
                _state.update { it.copy(month = it.month.minusMonths(1)) }; load()
            }
            AnalyticsEvent.NextMonth -> {
                _state.update { it.copy(month = it.month.plusMonths(1)) }; load()
            }
            is AnalyticsEvent.SetType -> {
                _state.update { it.copy(type = e.v) }; load()
            }
        }
    }

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val s = _state.value
            val data = getAnalytics(s.month, s.type)
            _state.update { it.copy(data = data, isLoading = false) }
        } catch (t: Throwable) {
            _state.update { it.copy(isLoading = false, error = t.message ?: "Ошибка") }
        }
    }
}
