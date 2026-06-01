package com.example.myapplication.Finance.ui.tagsgoals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.Finance.model.Goal
import com.example.domain.Finance.model.Tag
import com.example.myapplication.Finance.components.*
import com.example.myapplication.Finance.navigation.FinanceRoutes
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal

@Composable
fun TagsGoalsScreen(
    navController: NavController,
    vm: TagsGoalsViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(title = "Категории", subtitle = "Теги расходов, цели и калькулятор")

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FinanceColors.PrimaryDark)
            }
        }

        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                //теги

                SectionTitle(title = "Теги (категории расходов)")
                if (state.tags.isEmpty()) {
                    FinanceCard {
                        Text(
                            "Ещё нет тегов. Создайте первый.",
                            color = FinanceColors.TextSecondary, fontSize = 13.sp
                        )
                    }
                } else {
                    state.tags.forEach { t ->
                        TagCard(
                            tag = t,
                            onEdit = { navController.navigate(FinanceRoutes.EditTag.build(t.id)) },
                            onDelete = { vm.onEvent(TagsGoalsEvent.DeleteTag(t.id)) },
                        )
                    }
                }
                PrimaryButton(
                    text = "Создать тег",
                    onClick = { navController.navigate(FinanceRoutes.NewTag.route) },
                    modifier = Modifier.fillMaxWidth(),
                )

                //цели
                SectionTitle(title = "Цели (копилка)")
                if (state.goals.isEmpty()) {
                    FinanceCard {
                        Text(
                            "Ещё нет целей.",
                            color = FinanceColors.TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    state.goals.forEach { g ->
                        GoalCard(
                            goal = g,
                            onOpen = { navController.navigate(FinanceRoutes.GoalDetail.build(g.id)) },
                            onDelete = { vm.onEvent(TagsGoalsEvent.DeleteGoal(g.id)) },
                        )
                    }
                }
                PrimaryButton(
                    text = "Создать цель",
                    onClick = { navController.navigate(FinanceRoutes.NewGoal.route) },
                    modifier = Modifier.fillMaxWidth(),
                )

                //калькулятор
                SectionTitle(title = "Калькулятор вклада")
                CalculatorBlock(
                    sum = state.calcSum,
                    months = state.calcMonths,
                    percent = state.calcPercent,
                    result = state.calcResult,
                    onSum = { vm.onEvent(TagsGoalsEvent.CalcSum(it)) },
                    onMonths = { vm.onEvent(TagsGoalsEvent.CalcMonths(it)) },
                    onPercent = { vm.onEvent(TagsGoalsEvent.CalcPercent(it)) },
                    onCalc = { vm.onEvent(TagsGoalsEvent.Calc) },
                )

                state.error?.let { Text(it, color = FinanceColors.Expense, fontSize = 13.sp) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp,
        color = FinanceColors.TextPrimary)
}

@Composable
private fun TagCard(tag: Tag, onEdit: () -> Unit, onDelete: () -> Unit) {
    FinanceCard(onClick = onEdit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FinanceColors.PrimarySoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Bookmark, contentDescription = null, tint = FinanceColors.PrimaryDark)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(tag.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                    color = FinanceColors.TextPrimary)
                Text(
                    "Всего потрачено: ${formatMoney(tag.totalAmountSpent)} ₽",
                    color = FinanceColors.TextSecondary, fontSize = 12.sp,
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Изменить",
                    tint = FinanceColors.TextSecondary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить",
                    tint = FinanceColors.TextSecondary)
            }
        }
    }
}

@Composable
private fun GoalCard(goal: Goal, onOpen: () -> Unit, onDelete: () -> Unit) {
    val progress = if (goal.targetAmount.signum() > 0)
        (goal.currentAmount.toDouble() / goal.targetAmount.toDouble())
            .coerceIn(0.0, 1.0).toFloat()
    else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FinanceColors.CardBackground)
            .clickable(onClick = onOpen)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FinanceColors.HeaderGradient),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Flag, contentDescription = null, tint =Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(goal.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                    color = FinanceColors.TextPrimary)
                Text(
                    "Цель: ${formatMoney(goal.targetAmount)} Р" +
                            (goal.monthlyAmount?.let { " · в месяц ${formatMoney(it)} Р" } ?: ""),
                    color = FinanceColors.TextSecondary, fontSize = 12.sp,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить",
                    tint = FinanceColors.TextSecondary)
            }
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress },
            color = FinanceColors.PrimaryDark,
            trackColor = FinanceColors.Divider,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
        )
        Text(
            "Накоплено: ${formatMoney(goal.currentAmount)} Р из ${formatMoney(goal.targetAmount)} Р",
            color = FinanceColors.TextSecondary, fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun CalculatorBlock(
    sum: String, months: String, percent: String,
    result: BigDecimal?,
    onSum: (String) -> Unit,
    onMonths: (String) -> Unit,
    onPercent: (String) -> Unit,
    onCalc: () -> Unit,
) {
    FinanceCard {
        FinanceTextField(value = sum, onValueChange = onSum,
            label = "Сумма вклада, Р", keyboardType = KeyboardType.Decimal)
        Spacer(Modifier.height(8.dp))
        FinanceTextField(value = months, onValueChange = onMonths,
            label = "Срок, мес.", keyboardType = KeyboardType.Number)
        Spacer(Modifier.height(8.dp))
        FinanceTextField(value = percent, onValueChange = onPercent,
            label = "Годовой %", keyboardType = KeyboardType.Decimal)
        Spacer(Modifier.height(12.dp))
        PrimaryButton(text = "Рассчитать", onClick = onCalc, modifier = Modifier.fillMaxWidth())
        if (result != null) {
            Spacer(Modifier.height(10.dp))
            Text("Ожидаемый доход:", color = FinanceColors.TextSecondary, fontSize = 12.sp)
            Text("${formatMoney(result)} Р", fontSize = 20.sp,
                fontWeight = FontWeight.Bold, color = FinanceColors.PrimaryDark)
        }
    }
}
