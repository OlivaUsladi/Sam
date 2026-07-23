package com.example.domain.Feedback.use_case

import com.example.domain.Feedback.repository.FeedbackRepository

class SubmitFeedbackUseCase(private val repository: FeedbackRepository) {
    suspend operator fun invoke(subject: String, message: String): String {
        return repository.submitFeedback(subject, message)
    }
}
