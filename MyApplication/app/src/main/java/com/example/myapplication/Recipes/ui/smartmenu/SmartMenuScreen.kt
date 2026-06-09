package com.example.myapplication.Recipes.ui.smartmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.ai.WeeklyMenu
import org.koin.androidx.compose.koinViewModel

private val Background = Color(0xFFF5F5F5)
private val HeaderBackground = Color(0xFF1D1C22)
private val Accent = Color(0xFFE4DB40)
private val LinkBlue = Color(0xFF2670CC)
private val TextDark = Color(0xFF25232B)

@Composable
fun SmartMenuScreen(
    navController: NavController,
    viewModel: SmartMenuViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onEvent(SmartMenuEvent.LoadBudget) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeaderBackground)
                .padding(top = 12.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Назад",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
                Text(
                    text = "Умное меню",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.size(40.dp))
            }
        }

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Accent)
                    Spacer(Modifier.height(12.dp))
                    Text("Генерируем меню...", color = TextDark)
                }
            }

            state.generated -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.error?.let { err ->
                        item {
                            Text(err, color = Color(0xFFD24A4A), fontSize = 14.sp)
                        }
                    }
                    items(state.weeks, key = { it.weekNumber }) { week ->
                        WeekCard(
                            week = week,
                            added = state.addedWeeks.contains(week.weekNumber),
                            onAdd = { viewModel.onEvent(SmartMenuEvent.AddShoppingList(week.weekNumber)) }
                        )
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Составим меню и список покупок на основе вашего бюджета.",
                            modifier = Modifier.padding(16.dp),
                            color = TextDark,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedTextField(
                        value = state.budgetInput,
                        onValueChange = { viewModel.onEvent(SmartMenuEvent.SetBudget(it)) },
                        label = { Text("Бюджет на продукты, Р") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.autoDetectedBudget != null) {
                        Text(
                            "Автоматически: остаток по тегу «Продукты»",
                            color = LinkBlue,
                            fontSize = 12.sp
                        )
                    }

                    state.error?.let { err ->
                        Text(err, color = Color(0xFFD24A4A), fontSize = 14.sp)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(Accent, RoundedCornerShape(14.dp))
                            .clickable { viewModel.onEvent(SmartMenuEvent.Generate) }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Сгенерировать меню",
                            color = TextDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekCard(
    week: WeeklyMenu,
    added: Boolean,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Неделя ${week.weekNumber}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(Modifier.height(8.dp))
            week.days.forEachIndexed { index, day ->
                Text(
                    "День ${index + 1}",
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark,
                    fontSize = 14.sp
                )
                Text("Завтрак: ${day.breakfast}", color = TextDark, fontSize = 13.sp)
                Text("Обед: ${day.lunch}", color = TextDark, fontSize = 13.sp)
                Text("Ужин: ${day.dinner}", color = TextDark, fontSize = 13.sp)
                Text("Перекус: ${day.snack}", color = TextDark, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }

            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Text("Список покупок:", fontWeight = FontWeight.SemiBold, color = TextDark)
            week.shoppingItems.forEach { item ->
                val qty = buildString {
                    item.quantity?.let { append(" — ") ; append(formatQuantity(it)) }
                    item.unit?.let { append(" ") ; append(it) }
                }
                Text("• ${item.name}$qty", color = TextDark, fontSize = 13.sp)
            }

            Spacer(Modifier.height(12.dp))
            if (added) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE2E2E2), RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Добавлен ✓", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LinkBlue, RoundedCornerShape(12.dp))
                        .clickable(onClick = onAdd)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Добавить в списки покупок", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

private fun formatQuantity(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
