package com.example.myapplication.Finance.ui.bankreport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.BankReport
import com.example.domain.Finance.use_case.DeleteBankReportUseCase
import com.example.domain.Finance.use_case.GetBankReportsUseCase
import com.example.domain.Finance.use_case.ProcessBankReportUseCase
import com.example.domain.Finance.use_case.UploadBankReportUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//пока типа заглушки
data class BankReportsUiState(
    val reports: List<BankReport> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class BankReportsEvent {
    data object Reload : BankReportsEvent()
    data class Upload(val fileName: String, val sizeBytes: Long, val content: ByteArray) : BankReportsEvent()
    data class Delete(val id: Int) : BankReportsEvent()
    data class Process(val id: Int) : BankReportsEvent()
}

class BankReportsViewModel(
    private val getReports: GetBankReportsUseCase,
    private val uploadReport: UploadBankReportUseCase,
    private val deleteReport: DeleteBankReportUseCase,
    private val processReport: ProcessBankReportUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(BankReportsUiState())
    val state: StateFlow<BankReportsUiState> = _state.asStateFlow()

    init { load() }

    fun onEvent(e: BankReportsEvent) {
        when (e) {
            BankReportsEvent.Reload     -> load()
            is BankReportsEvent.Upload  -> upload(e.fileName, e.sizeBytes, e.content)
            is BankReportsEvent.Delete  -> remove(e.id)
            is BankReportsEvent.Process -> process(e.id)
        }
    }

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try { _state.update { it.copy(reports = getReports(), isLoading = false) } }
        catch (t: Throwable) { _state.update { it.copy(isLoading = false, error = t.message ?: "Ошибка") } }
    }

    private fun upload(fileName: String, sizeBytes: Long, content: ByteArray) = viewModelScope.launch {
        try { uploadReport(fileName, sizeBytes, content); load() }
        catch (t: Throwable) { _state.update { it.copy(error = t.message ?: "Не удалось загрузить") } }
    }

    private fun remove(id: Int) = viewModelScope.launch {
        try { deleteReport(id); load() }
        catch (t: Throwable) { _state.update { it.copy(error = t.message ?: "Не удалось удалить") } }
    }

    private fun process(id: Int) = viewModelScope.launch {
        try { processReport(id); load() }
        catch (t: Throwable) { _state.update { it.copy(error = t.message ?: "Не удалось обработать") } }
    }
}
