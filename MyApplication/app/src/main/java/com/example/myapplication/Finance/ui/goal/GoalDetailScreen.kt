package com.example.myapplication.Finance.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.Finance.components.*
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun GoalDetailScreen(
    navController: NavController,
    id: Int,
    vm: GoalDetailViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(id) { vm.onEvent(GoalDetailEvent.Init(id)) }
    LaunchedEffect(state.deleted) { if (state.deleted) navController.navigateUp() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(
            title = state.goal?.name ?: "Цель",
            subtitle = state.goal?.description ?: "",
            actionIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onAction = { navController.navigateUp() },
        )

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = FinanceColors.PrimaryDark)
                }
            }

            state.error!=null -> {
                Text(text = "Ошибка: ${state.error}", color = FinanceColors.Expense, fontSize = 13.sp)
            }

            else -> {
                val g = state.goal
                val progress = if (g!!.targetAmount.signum() > 0)
                    (g.currentAmount.toDouble() / g.targetAmount.toDouble())
                        .coerceIn(0.0, 1.0).toFloat()
                else 0f

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FinanceCard {
                        Text("Прогресс", color = FinanceColors.TextSecondary, fontSize = 12.sp)
                        LinearProgressIndicator(
                            progress = { progress },
                            color = FinanceColors.PrimaryDark,
                            trackColor = FinanceColors.Divider,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .padding(top = 6.dp),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Накоплено: ${formatMoney(g.currentAmount)} Р из ${formatMoney(g.targetAmount)} Р",
                            color = FinanceColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    FinanceCard {
                        if (!g.description.isNullOrBlank()) {
                            Text("Описание", color = FinanceColors.TextSecondary, fontSize = 12.sp)
                            Text(
                                g.description ?: "",
                                color = FinanceColors.TextPrimary,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        Text("Цель", color = FinanceColors.TextSecondary, fontSize = 12.sp)
                        Text(
                            "${formatMoney(g.targetAmount)} ₽",
                            color = FinanceColors.TextPrimary, fontWeight = FontWeight.SemiBold
                        )
                        if (g.targetDate != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "До какого числа",
                                color = FinanceColors.TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                g.targetDate!!.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                                color = FinanceColors.TextPrimary, fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (g.monthlyAmount != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Ежемесячное внесение",
                                color = FinanceColors.TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                "${formatMoney(g.monthlyAmount!!)} ₽",
                                color = FinanceColors.TextPrimary, fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    FinanceCard {
                        Text(
                            "Пополнить накопление",
                            color = FinanceColors.TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        FinanceTextField(
                            value = state.addAmount,
                            onValueChange = { vm.onEvent(GoalDetailEvent.SetAdd(it)) },
                            label = "Сумма, ₽",
                            keyboardType = KeyboardType.Decimal,
                        )
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton(
                            text = "Добавить",
                            onClick = { vm.onEvent(GoalDetailEvent.AddToCurrent) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    SecondaryButton(
                        text = "Удалить цель",
                        onClick = { vm.onEvent(GoalDetailEvent.Delete) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
