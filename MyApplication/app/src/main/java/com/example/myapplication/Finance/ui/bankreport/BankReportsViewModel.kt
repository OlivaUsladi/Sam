package com.example.myapplication.Finance.ui.bankreport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.ImportReport
import com.example.domain.Finance.model.Source
import com.example.domain.Finance.use_case.GetSourcesUseCase
import com.example.domain.Finance.use_case.ImportBankReportUseCase
import com.example.domain.Finance.use_case.ObserveFinanceChangesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BankReportsUiState(
    val sources: List<Source> = emptyList(),
    val selectedSourceId: Int? = null,
    val isUploading: Boolean = false,
    val lastReport: ImportReport? = null,
    val error: String? = null,
)

sealed class BankReportsEvent {
    data class SelectSource(val id: Int) : BankReportsEvent()
    data class Import(val fileName: String, val content: ByteArray) : BankReportsEvent()
    data object Reset : BankReportsEvent()
}

class BankReportsViewModel(
    private val getSources: GetSourcesUseCase,
    private val importReport: ImportBankReportUseCase,
    private val observeChanges: ObserveFinanceChangesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(BankReportsUiState())
    val state: StateFlow<BankReportsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch { observeChanges().collectLatest { loadSources() } }
    }

    fun onEvent(e: BankReportsEvent) {
        when (e) {
            is BankReportsEvent.SelectSource -> _state.update { it.copy(selectedSourceId = e.id) }
            is BankReportsEvent.Import -> doImport(e.fileName, e.content)
            BankReportsEvent.Reset -> _state.update { it.copy(lastReport = null, error = null) }
        }
    }

    private fun loadSources() = viewModelScope.launch {
        try {
            val sources = getSources()
            _state.update {
                it.copy(sources = sources,
                    selectedSourceId = it.selectedSourceId ?: sources.firstOrNull()?.id)
            }
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message ?: "Не удалось загрузить источники") }
        }
    }

    private fun doImport(fileName: String, content: ByteArray) = viewModelScope.launch {
        val srcId = _state.value.selectedSourceId
        if (srcId == null) {
            _state.update { it.copy(error = "Сначала выберите счёт") }
            return@launch
        }
        _state.update { it.copy(isUploading = true, error = null, lastReport = null) }
        try {
            val r = importReport(fileName, srcId, content)
            _state.update { it.copy(isUploading = false, lastReport = r) }
        } catch (t: Throwable) {
            _state.update { it.copy(isUploading = false, error = t.message ?: "Не удалось импортировать") }
        }
    }
}
