package com.example.myapplication.Hints

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.data.Hints.converter.ConverterToJson
import com.example.myapplication.Hints.ui.theme.MyApplicationTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Greeting()
        }
    }
}

@Composable
fun Greeting() {
    //----------------------------------------------------------
    //Функция чтения файла и передачи в конвертер для преобразования текстового файла в json
    val context = LocalContext.current
    val converterToJson = ConverterToJson()

    LaunchedEffect(Unit) {
        val content = context.assets.open("Статья прикрепление к поликлинике.txt")
            .bufferedReader().use { it.readText() }

        val blocks = converterToJson.convertFromTXT(content)
        val json = converterToJson.convertBlocksToJson(blocks)
        println(json)
    }
    //-------------------------------------------------------------

    Text("work work")
}
