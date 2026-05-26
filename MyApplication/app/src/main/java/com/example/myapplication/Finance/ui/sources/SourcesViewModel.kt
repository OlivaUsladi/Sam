package com.example.myapplication.Finance.ui.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.Source
import com.example.domain.Finance.model.SourceType
import com.example.domain.Finance.use_case.CreateSourceUseCase
import com.example.domain.Finance.use_case.DeleteSourceUseCase
import com.example.domain.Finance.use_case.GetSourcesUseCase
import com.example.domain.Finance.use_case.UpdateSourceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SourcesUiState(
    val sources: List<Source> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val editing: Source? = null,
    val showCreateDialog: Boolean = false,
)

sealed class SourcesEvent {
    data object Reload : SourcesEvent()
    data object OpenCreate : SourcesEvent()
    data class OpenEdit(val s: Source) : SourcesEvent()
    data object Dismiss : SourcesEvent()
    data class Save(val id: Int?, val name: String, val type: SourceType) : SourcesEvent()
    data class Delete(val id: Int) : SourcesEvent()
}

class SourcesViewModel(
    private val getSources: GetSourcesUseCase,
    private val createSource: CreateSourceUseCase,
    private val updateSource: UpdateSourceUseCase,
    private val deleteSource: DeleteSourceUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SourcesUiState())
    val state: StateFlow<SourcesUiState> = _state.asStateFlow()

    init { load() }

    fun onEvent(e: SourcesEvent) {
        when (e) {
            SourcesEvent.Reload      -> load()
            SourcesEvent.OpenCreate  -> _state.update { it.copy(showCreateDialog = true, editing = null) }
            is SourcesEvent.OpenEdit -> _state.update { it.copy(showCreateDialog = true, editing = e.s) }
            SourcesEvent.Dismiss     -> _state.update { it.copy(showCreateDialog = false, editing = null) }
            is SourcesEvent.Save     -> save(e.id, e.name, e.type)
            is SourcesEvent.Delete   -> remove(e.id)
        }
    }

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            _state.update { it.copy(sources = getSources(), isLoading = false) }
        } catch (t: Throwable) {
            _state.update { it.copy(isLoading = false, error = t.message ?: "Ошибка") }
        }
    }

    private fun save(id: Int?, name: String, type: SourceType) = viewModelScope.launch {
        try {
            if (id == null) createSource(name, type) else updateSource(id, name, type)
            _state.update { it.copy(showCreateDialog = false, editing = null) }
            load()
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message ?: "Не удалось сохранить") }
        }
    }

    private fun remove(id: Int) = viewModelScope.launch {
        try { deleteSource(id); load() }
        catch (t: Throwable) { _state.update { it.copy(error = t.message ?: "Не удалось удалить") } }
    }
}
