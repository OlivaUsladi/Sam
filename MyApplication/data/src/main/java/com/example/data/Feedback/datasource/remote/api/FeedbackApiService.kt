package com.example.data.Feedback.datasource.remote.api

import com.example.data.Feedback.datasource.remote.dto.FeedbackRequestDto
import com.example.data.Feedback.datasource.remote.dto.FeedbackResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface FeedbackApiService {

    @POST("api/feedback")
    suspend fun submitFeedback(@Body req: FeedbackRequestDto): FeedbackResponseDto
}
