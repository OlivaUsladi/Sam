package com.example.myapplication.Hints

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Hints.converter.ConverterToJson
import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.model.ContentBlock
import com.example.domain.Hints.model.TextArea
import com.example.domain.Hints.model.TextStyle


//2. Разобраться с Image где хранить и как доставать
//3. Добавить в статью Image

@Composable
fun ArticleScreen(id: Int){
    Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
        Spacer(modifier = Modifier.height(100.dp))
        val context = LocalContext.current
        val converterToJson = ConverterToJson()

        var allArticleContent = remember { mutableStateListOf<ArticleContent>() }

        allArticleContent.add(ArticleContent(
            articleId = 1,
            checklistId = 1,
            blocks = listOf(
                ContentBlock.Paragraph(
                    text = "Как управлять временем",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Тайм-менеджмент — это искусство управлять временем. В современном мире, где каждый день наполнен задачами и обязательствами, умение правильно распределять свое время становится ключевым навыком успешного человека.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Основные принципы тайм-менеджмента:",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "1. Матрица Эйзенхауэра\nРазделяйте дела на важные и срочные. Это поможет сфокусироваться на действительно значимых задачах и не тратить время на второстепенные.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "2. Метод Помодоро\nРаботайте интервалами: 25 минут работы, 5 минут отдыха. После 4 циклов сделайте длинный перерыв 15-30 минут.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "3. Правило 2 минут\nЕсли дело занимает меньше 2 минут — сделайте его сразу. Не откладывайте мелкие задачи на потом.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Планирование дня",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Начинайте каждый день с составления плана. Выделите 3 самые важные задачи, которые нужно выполнить сегодня. Используйте ежедневник или приложения для планирования.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Борьба с прокрастинацией",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Прокрастинация — главный враг продуктивности. Чтобы побороть желание отложить дела на потом, разбивайте большие задачи на маленькие шаги и начинайте с самого простого.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                )
            )
        ))

        allArticleContent.add(ArticleContent(
            articleId = 2,
            checklistId = 2,
            blocks = listOf(
                ContentBlock.Paragraph(
                    text = "Техники продуктивности",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Существует множество техник продуктивности, которые помогают работать эффективнее и успевать больше. Рассмотрим самые популярные и действенные методы.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Метод Pomodoro",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Техника предполагает работу интервалами по 25 минут, после каждого интервала следует 5-минутный перерыв. После 4 таких циклов делается длинный перерыв 15-30 минут. Это помогает сохранять концентрацию и не переутомляться.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Метод GTD (Getting Things Done)",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Методология Дэвида Аллена учит фиксировать все задачи и идеи в надежной системе, освобождая голову для творчества и решения текущих задач. Важно регулярно пересматривать и обновлять списки дел.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Матрица Эйзенхауэра",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Разделите все дела на 4 категории:\n• Важные и срочные — делайте сразу\n• Важные, но не срочные — планируйте\n• Срочные, но не важные — делегируйте\n• Не важные и не срочные — удаляйте",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                )
            )
        ))

        allArticleContent.add(ArticleContent(
            articleId = 3,
            checklistId = 3,
            blocks = listOf(
                ContentBlock.Paragraph(
                    text = "Медитация для начинающих",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Медитация — это практика тренировки ума, которая помогает развить осознанность, снизить стресс и улучшить концентрацию. Для начинающих важно начать с простых техник и постепенно увеличивать время практики.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "С чего начать?",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Начните с 5-10 минут в день. Найдите тихое место, сядьте удобно, закройте глаза и сосредоточьтесь на дыхании. Когда мысли отвлекают, просто возвращайте внимание к дыханию без осуждения.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Простые техники для новичков:",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "1. Медитация на дыхание\nСледите за естественным дыханием, ощущая, как воздух входит и выходит через нос.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "2. Сканирование тела\nМысленно проходитесь вниманием по всему телу от макушки до пальцев ног, замечая ощущения.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "3. Медитация с мантрой\nПовторяйте про себя слово или фразу, чтобы удерживать фокус внимания.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                )
            )
        ))

        allArticleContent.add(ArticleContent(
            articleId = 4,
            checklistId = 4,
            blocks = listOf(
                ContentBlock.Paragraph(
                    text = "Здоровый сон и режим",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Качественный сон — основа здоровья и продуктивности. Во сне организм восстанавливается, обрабатывает информацию и накапливает энергию для нового дня.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Сколько нужно спать?",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Взрослому человеку требуется 7-9 часов сна. Важна не только продолжительность, но и регулярность — ложитесь и вставайте в одно и то же время, даже в выходные.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Правила гигиены сна:",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "1. За час до сна откажитесь от гаджетов\nСиний свет экранов подавляет выработку мелатонина — гормона сна.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "2. Создайте ритуал отхода ко сну\nТеплая ванна, чтение книги, легкая растяжка помогут настроиться на сон.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "3. Обеспечьте комфортные условия\nВ спальне должно быть темно, тихо и прохладно (18-20°C).",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "4. Не ешьте перед сном\nПоследний прием пищи должен быть за 2-3 часа до сна.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                )
            )
        ))

        allArticleContent.add(ArticleContent(
            articleId = 5,
            checklistId = 5,
            blocks = listOf(
                ContentBlock.Paragraph(
                    text = "Утренние ритуалы",
                    style = TextStyle.Bold,
                    size = 24,
                    area = TextArea.Center
                ),
                ContentBlock.Paragraph(
                    text = "Утро задает тон всему дню. Правильные утренние привычки помогают проснуться, настроиться на продуктивный лад и сохранять энергию до вечера.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Идеальное утро: с чего начать?",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Вставайте без телефона. Первые 30 минут после пробуждения посвятите себе, а не проверке соцсетей и новостей.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "Полезные утренние привычки:",
                    style = TextStyle.Bold,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "1. Стакан воды\nВыпейте стакан теплой воды с лимоном — это запустит пищеварение и восполнит жидкость после сна.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "2. Легкая зарядка\n5-10 минут растяжки или йоги разбудят тело и улучшат кровообращение.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "3. Медитация или дыхательная практика\n5 минут осознанного дыхания помогут успокоить ум и настроиться на день.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "4. Планирование дня\nЗапишите 3 главные задачи на день. Это поможет сфокусироваться на важном.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                ),
                ContentBlock.Paragraph(
                    text = "5. Полезный завтрак\nНе пропускайте завтрак. Включите в него белки, сложные углеводы и полезные жиры для энергии.",
                    style = TextStyle.Normal,
                    size = 14,
                    area = TextArea.Left
                )
            )
        ))

        LaunchedEffect(Unit) {
            val content = context.assets.open("Статья прикрепление к поликлинике.txt")
                .bufferedReader().use { it.readText() }

            var blocks = converterToJson.convertFromTXT(content)
            val json = converterToJson.convertBlocksToJson(blocks)
            blocks = converterToJson.convertJsonToBlocks(json)

            allArticleContent.add(ArticleContent(articleId = 6, blocks=blocks, checklistId = 6))

        }

        val article = allArticleContent.filter { it.articleId == id }
        val articleContent = article.firstOrNull()

        if (articleContent == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Статья не найдена",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        }

        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(articleContent!!.blocks) { block ->
                    when (block) {
                        is ContentBlock.Paragraph -> {
                            ParagraphBlock(block)
                        }

                        is ContentBlock.Image -> {
                            // Доделать image
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun ParagraphBlock(paragraph: ContentBlock.Paragraph) {

    //Расположение
    val textAlign = when (paragraph.area) {
        TextArea.Left -> TextAlign.Left
        TextArea.Center -> TextAlign.Center
        TextArea.Right -> TextAlign.Right
    }

    //Тип шрифта (стиль)
    val fontWeight = when (paragraph.style) {
        TextStyle.Normal -> FontWeight.Normal
        TextStyle.Bold -> FontWeight.Bold
        TextStyle.Italic -> FontWeight.Normal
        TextStyle.Underlined -> FontWeight.Normal
    }

    //до информация для Italic
    val fontStyle = when (paragraph.style) {
        TextStyle.Italic -> androidx.compose.ui.text.font.FontStyle.Italic
        else -> androidx.compose.ui.text.font.FontStyle.Normal
    }

    //доп информация для подчёркивания
    val textDecoration = when (paragraph.style) {
        TextStyle.Underlined -> androidx.compose.ui.text.style.TextDecoration.Underline
        else -> androidx.compose.ui.text.style.TextDecoration.None
    }

    //Верхний отступ
    val topPadding = when {
        paragraph.style == TextStyle.Bold && paragraph.size == 24 -> 16.dp
        paragraph.style == TextStyle.Bold -> 24.dp
        else -> 8.dp
    }

    //Нижний отступ
    val bottomPadding = when {
        paragraph.style == TextStyle.Bold && paragraph.size == 24 -> 8.dp
        else -> 4.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding, bottom = bottomPadding)
    ) {
        Text(
            text = paragraph.text,
            fontSize = paragraph.size.sp,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            textDecoration = textDecoration,
            textAlign = textAlign,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
    }
}