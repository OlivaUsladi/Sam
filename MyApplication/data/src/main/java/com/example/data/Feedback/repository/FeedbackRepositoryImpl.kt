package com.example.data.Feedback.repository

import com.example.data.Feedback.datasource.remote.api.FeedbackApiService
import com.example.data.Feedback.datasource.remote.dto.FeedbackRequestDto
import com.example.domain.Feedback.repository.FeedbackRepository

class FeedbackRepositoryImpl(
    private val api: FeedbackApiService
) : FeedbackRepository {

    override suspend fun submitFeedback(subject: String, message: String): String {
        val response = api.submitFeedback(FeedbackRequestDto(subject, message))
        return response.message
    }
}
