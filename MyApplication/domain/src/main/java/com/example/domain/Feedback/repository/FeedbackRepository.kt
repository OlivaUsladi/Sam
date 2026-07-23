package com.example.domain.Feedback.repository

interface FeedbackRepository {
    suspend fun submitFeedback(subject: String, message: String): String
}
