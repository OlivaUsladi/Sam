package com.example.myapplication.Finance.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.Source
import com.example.domain.Finance.model.Tag
import com.example.domain.Finance.model.Transaction
import com.example.domain.Finance.model.TransactionType
import com.example.domain.Finance.use_case.CreateTagUseCase
import com.example.domain.Finance.use_case.CreateTransactionUseCase
import com.example.domain.Finance.use_case.GetSourcesUseCase
import com.example.domain.Finance.use_case.GetTagsUseCase
import com.example.domain.Finance.use_case.GetTransactionsUseCase
import com.example.domain.Finance.use_case.UpdateTransactionUseCase
import com.example.domain.Finance.use_case.CreateSourceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate

data class TransactionEditUiState(
    val id: Int? = null,
    val name: String = "",
    val amount: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val type: TransactionType = TransactionType.EXPENSE,
    val sourceId: Int? = null,
    val tagId: Int? = null,
    val sources: List<Source> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

sealed class TransactionEditEvent {
    data class Init(val id: Int?) : TransactionEditEvent()
    data class SetName(val v: String) : TransactionEditEvent()
    data class SetAmount(val v: String) : TransactionEditEvent()
    data class SetDescription(val v: String) : TransactionEditEvent()
    data class SetDate(val v: LocalDate) : TransactionEditEvent()
    data class SetType(val v: TransactionType) : TransactionEditEvent()
    data class SetSource(val id: Int) : TransactionEditEvent()
    data class SetTag(val id: Int?) : TransactionEditEvent()
    data object Save : TransactionEditEvent()
}

class TransactionEditViewModel(
    private val getTransactions: GetTransactionsUseCase,
    private val getSources: GetSourcesUseCase,
    private val getTags: GetTagsUseCase,
    private val createTx: CreateTransactionUseCase,
    private val updateTx: UpdateTransactionUseCase,
    private val createSource: CreateSourceUseCase,
    private val createTag: CreateTagUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionEditUiState())
    val state: StateFlow<TransactionEditUiState> = _state.asStateFlow()

    fun onEvent(e: TransactionEditEvent) {
        when (e) {
            is TransactionEditEvent.Init           -> load(e.id)
            is TransactionEditEvent.SetName        -> _state.update { it.copy(name = e.v) }
            is TransactionEditEvent.SetAmount      -> _state.update { it.copy(amount = e.v.filter {
                c -> c.isDigit() || c == '.' || c == ',' }) }
            is TransactionEditEvent.SetDescription -> _state.update { it.copy(description = e.v) }
            is TransactionEditEvent.SetDate        -> _state.update { it.copy(date = e.v) }
            is TransactionEditEvent.SetType        -> _state.update {

                it.copy(type = e.v, tagId = if (e.v == TransactionType.INCOME) null else it.tagId)
            }
            is TransactionEditEvent.SetSource -> _state.update { it.copy(sourceId = e.id) }
            is TransactionEditEvent.SetTag -> _state.update { it.copy(tagId = e.id) }
            TransactionEditEvent.Save -> save()
        }
    }

    private fun load(id: Int?) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        try {
            val srcs = getSources()
            val tags = getTags()
            val tx: Transaction? = id?.let { tid -> getTransactions(null).firstOrNull { it.id == tid } }
            _state.update {
                it.copy(
                    id = tx?.id,
                    name = tx?.name.orEmpty(),
                    amount = tx?.amount?.toPlainString().orEmpty(),
                    description = tx?.description.orEmpty(),
                    date = tx?.date ?: LocalDate.now(),
                    type = tx?.type ?: TransactionType.EXPENSE,
                    sourceId = tx?.sourceId ?: srcs.firstOrNull()?.id,
                    tagId = tx?.tagId,
                    sources = srcs,
                    tags = tags,
                    isLoading = false,
                    error = null,
                )
            }
        } catch (t: Throwable) {
            _state.update { it.copy(isLoading = false, error = t.message ?: "Ошибка загрузки") }
        }
    }

    private fun save() = viewModelScope.launch {
        val s = _state.value
        val amount = s.amount.replace(',', '.').toBigDecimalOrNull()
        if (s.name.isBlank() || amount == null || amount <= BigDecimal.ZERO || s.sourceId == null) {
            _state.update { it.copy(error = "Заполните название, сумму и источник") }
            return@launch
        }
        try {
            if (s.id == null) {
                createTx(
                    name = s.name.trim(),
                    amount = amount,
                    type = s.type,
                    description = s.description.ifBlank { null },
                    date = s.date,
                    sourceId = s.sourceId,
                    tagId = if (s.type == TransactionType.EXPENSE) s.tagId else null,
                )
            } else {
                updateTx(
                    id = s.id,
                    name = s.name.trim(),
                    amount = amount,
                    type = s.type,
                    description = s.description.ifBlank { null },
                    date = s.date,
                    sourceId = s.sourceId,
                    tagId = if (s.type == TransactionType.EXPENSE) s.tagId else null,
                )
            }
            _state.update { it.copy(saved = true, error = null) }
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message ?: "Не удалось сохранить") }
        }
    }
}
