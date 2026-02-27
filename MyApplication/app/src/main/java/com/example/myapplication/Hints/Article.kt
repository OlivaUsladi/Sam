package com.example.myapplication.Hints

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//1. Создать статьи AricleContent на темы:
// Как управлять временем
//Техники продуктивности
//Медитация для начинающих
//Здоровый сон и режим
//Утренние ритуалы
//2. Разобраться с Image где хранить и как доставать
//3. Создать страницу с выводом контента

@Composable
fun ArticleScreen(id: Int){
    Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        Spacer(modifier = Modifier.height(100.dp))
        val idString = id.toString()
        Text(text = idString, fontSize = 24.sp, textAlign = TextAlign.Center)
    }
}