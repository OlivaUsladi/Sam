package com.example.myapplication.Profile.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    onBack: () -> Unit,
    viewModel: FeedbackViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Обратная связь", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.success) {
                SuccessContent(onBack = onBack, onNewMessage = { viewModel.reset() })
            } else {
                FeedbackForm(state, viewModel)
            }
        }
    }
}

@Composable
private fun FeedbackForm(state: FeedbackUiState, viewModel: FeedbackViewModel) {
    Spacer(Modifier.height(24.dp))

    Text(
        text = "Есть вопрос или предложение?\nНапишите нам!",
        color = Color(0xFF1A1A1A),
        fontSize = 16.sp,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(8.dp))

    Text(
        text = "Или напишите на support@sam-app.ru",
        color = Color(0xFF6B6B6B),
        fontSize = 14.sp,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(24.dp))

    OutlinedTextField(
        value = state.subject,
        onValueChange = viewModel::onSubjectChange,
        label = { Text("Тема") },
        placeholder = { Text("Например: Предложение по функции") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFEF6C00),
            cursorColor = Color(0xFFEF6C00)
        )
    )

    Spacer(Modifier.height(16.dp))

    OutlinedTextField(
        value = state.message,
        onValueChange = viewModel::onMessageChange,
        label = { Text("Сообщение") },
        placeholder = { Text("Опишите ваш вопрос или предложение...") },
        minLines = 5,
        maxLines = 10,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFEF6C00),
            cursorColor = Color(0xFFEF6C00)
        )
    )

    if (state.error != null) {
        Spacer(Modifier.height(8.dp))
        Text(state.error, color = Color(0xFFD32F2F), fontSize = 14.sp)
    }

    Spacer(Modifier.height(24.dp))

    if (state.loading) {
        CircularProgressIndicator(color = Color(0xFFEF6C00), modifier = Modifier.size(36.dp))
    } else {
        Button(
            onClick = viewModel::submit,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = state.subject.isNotBlank() && state.message.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00))
        ) {
            Text("Отправить", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SuccessContent(onBack: () -> Unit, onNewMessage: () -> Unit) {
    Spacer(Modifier.height(80.dp))

    Text(
        text = "Спасибо!",
        color = Color(0xFF1A1A1A),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(12.dp))

    Text(
        text = "Ваше сообщение отправлено.\nМы ответим в ближайшее время.",
        color = Color(0xFF6B6B6B),
        fontSize = 16.sp,
        textAlign = TextAlign.Center
    )

    Spacer(Modifier.height(32.dp))

    Button(
        onClick = onBack,
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00))
    ) {
        Text("Вернуться в профиль", fontSize = 15.sp)
    }

    Spacer(Modifier.height(12.dp))

    Button(
        onClick = onNewMessage,
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
    ) {
        Text("Написать ещё", fontSize = 15.sp)
    }
}
