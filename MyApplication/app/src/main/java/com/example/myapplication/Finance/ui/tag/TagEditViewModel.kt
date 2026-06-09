package com.example.myapplication.Finance.ui.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Finance.model.Tag
import com.example.domain.Finance.model.Transaction
import com.example.domain.Finance.use_case.CreateTagUseCase
import com.example.domain.Finance.use_case.DeleteTagUseCase
import com.example.domain.Finance.use_case.GetTagsUseCase
import com.example.domain.Finance.use_case.GetTransactionsByTagUseCase
import com.example.domain.Finance.use_case.UpdateTagUseCase
import com.example.domain.Finance.use_case.AssignTagToTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class TagEditUiState(
    val id: Int? = null,
    val name: String = "",
    val monthlyLimit: String = "",
    val totalSpent: BigDecimal = BigDecimal.ZERO,
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)

sealed class TagEditEvent {
    data class Init(val id: Int?) : TagEditEvent()
    data class SetName(val v: String) : TagEditEvent()
    data class SetMonthlyLimit(val v: String) : TagEditEvent()
    data object Save : TagEditEvent()
    data object Delete : TagEditEvent()
}

class TagEditViewModel(
    private val getTags: GetTagsUseCase,
    private val createTag: CreateTagUseCase,
    private val updateTag: UpdateTagUseCase,
    private val deleteTag: DeleteTagUseCase,
    private val getTxByTag: GetTransactionsByTagUseCase,
    @Suppress("unused") private val assignTagToTx: AssignTagToTransactionsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TagEditUiState())
    val state: StateFlow<TagEditUiState> = _state.asStateFlow()

    fun onEvent(e: TagEditEvent) {
        when (e) {
            is TagEditEvent.Init    -> load(e.id)
            is TagEditEvent.SetName -> _state.update { it.copy(name = e.v) }
            is TagEditEvent.SetMonthlyLimit -> _state.update { it.copy(monthlyLimit = e.v) }
            TagEditEvent.Save       -> save()
            TagEditEvent.Delete     -> remove()
        }
    }

    private fun load(id: Int?) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            if (id == null) {
                _state.update { it.copy(id = null, name = "", monthlyLimit = "", totalSpent = BigDecimal.ZERO,
                    transactions = emptyList(), isLoading = false) }
            } else {
                val tag: Tag? = getTags().firstOrNull { it.id == id }
                val txs = if (tag != null) getTxByTag(id) else emptyList()
                _state.update {
                    it.copy(
                        id = tag?.id,
                        name = tag?.name.orEmpty(),
                        monthlyLimit = tag?.monthlyLimit?.toPlainString().orEmpty(),
                        totalSpent = tag?.totalAmountSpent ?: BigDecimal.ZERO,
                        transactions = txs,
                        isLoading = false,
                    )
                }
            }
        } catch (t: Throwable) {
            _state.update { it.copy(isLoading = false, error = t.message ?: "Ошибка загрузки") }
        }
    }

    private fun save() = viewModelScope.launch {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(error = "Введите название тэга") }
            return@launch
        }
        val limit = s.monthlyLimit.trim().let { raw ->
            if (raw.isEmpty()) null else raw.replace(',', '.').toBigDecimalOrNull()
        }
        if (s.monthlyLimit.isNotBlank() && limit == null) {
            _state.update { it.copy(error = "Некорректный лимит") }
            return@launch
        }
        try {
            val saved = if (s.id == null) createTag(s.name.trim(), limit)
            else updateTag(s.id, s.name.trim(), limit)
            _state.update { it.copy(saved = true, id = saved.id, error = null) }
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message ?: "Не удалось сохранить") }
        }
    }

    private fun remove() = viewModelScope.launch {
        val s = _state.value
        if (s.id == null) return@launch
        try {
            deleteTag(s.id)
            _state.update { it.copy(saved = true) }
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message ?: "Не удалось удалить") }
        }
    }
}
