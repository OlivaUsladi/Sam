package com.example.myapplication.Finance.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.Source
import com.example.domain.Finance.use_case.GetSourcesUseCase
import com.example.domain.Finance.use_case.ObserveFinanceChangesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountsUiState(
    val sources: List<Source> = emptyList(),
    val selectedSourceId: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class AccountsViewModel(
    private val getSources: GetSourcesUseCase,
    private val observeChanges: ObserveFinanceChangesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountsUiState())
    val state: StateFlow<AccountsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch { observeChanges().collectLatest { reload() } }
    }

    fun reload() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val srcs = getSources()
            _state.update { it.copy(sources = srcs, isLoading = false) }
        } catch (t: Throwable) {
            _state.update { it.copy(isLoading = false, error = t.message ?: "Ошибка загрузки") }
        }
    }

    fun selectSource(id: Int?) {
        _state.update { it.copy(selectedSourceId = id) }
    }
}
