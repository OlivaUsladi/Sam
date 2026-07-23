package com.example.myapplication.di.feedback

import com.example.data.Feedback.repository.FeedbackRepositoryImpl
import com.example.data.Recipes.datasource.remote.RetrofitClient
import com.example.domain.Feedback.repository.FeedbackRepository
import org.koin.dsl.module

val feedbackDataModule = module {
    single { RetrofitClient.feedbackApiService }
    single<FeedbackRepository> { FeedbackRepositoryImpl(get()) }
}
