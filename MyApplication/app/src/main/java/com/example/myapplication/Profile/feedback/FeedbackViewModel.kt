package com.example.myapplication.Profile.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Feedback.use_case.SubmitFeedbackUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedbackUiState(
    val subject: String = "",
    val message: String = "",
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class FeedbackViewModel(
    private val submitFeedbackUseCase: SubmitFeedbackUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FeedbackUiState())
    val state: StateFlow<FeedbackUiState> = _state.asStateFlow()

    fun onSubjectChange(value: String) {
        _state.update { it.copy(subject = value, error = null) }
    }

    fun onMessageChange(value: String) {
        _state.update { it.copy(message = value, error = null) }
    }

    fun submit() {
        val s = _state.value
        if (s.subject.isBlank()) {
            _state.update { it.copy(error = "Укажите тему сообщения") }
            return
        }
        if (s.message.isBlank()) {
            _state.update { it.copy(error = "Введите сообщение") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching {
                submitFeedbackUseCase(s.subject.trim(), s.message.trim())
            }.fold(
                onSuccess = {
                    _state.update { it.copy(loading = false, success = true) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(loading = false, error = e.message ?: "Не удалось отправить")
                    }
                }
            )
        }
    }

    fun reset() {
        _state.value = FeedbackUiState()
    }
}
