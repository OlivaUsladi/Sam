package com.example.myapplication.di.feedback

import com.example.domain.Feedback.use_case.SubmitFeedbackUseCase
import org.koin.dsl.module

val feedbackDomainModule = module {
    factory { SubmitFeedbackUseCase(get()) }
}
