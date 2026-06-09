package com.example.myapplication.Finance.ui.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.example.domain.Finance.model.Transaction
import com.example.domain.Finance.model.TransactionType
import java.math.BigDecimal
import com.example.myapplication.Finance.components.*
import com.example.myapplication.Finance.navigation.FinanceRoutes
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TagEditScreen(
    navController: NavController,
    id: Int?,
    vm: TagEditViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(id) { vm.onEvent(TagEditEvent.Init(id)) }
    LaunchedEffect(state.saved) {
        if (state.saved) navController.navigateUp()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(
            title = if (id == null) "Новый тег" else "Изменить тег",
            subtitle = "Категория расходов",
            actionIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onAction = { navController.navigateUp() },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FinanceTextField(value = state.name,
                onValueChange = { vm.onEvent(TagEditEvent.SetName(it)) },
                label = "Название тега")

            FinanceTextField(value = state.monthlyLimit,
                onValueChange = { vm.onEvent(TagEditEvent.SetMonthlyLimit(it)) },
                label = "Лимит на месяц (необязательно)",
                keyboardType = KeyboardType.Decimal)

            val limitValue = state.monthlyLimit.trim().replace(',', '.').toBigDecimalOrNull()
            if (limitValue != null && limitValue.signum() > 0) {
                val now = LocalDate.now()
                val spentThisMonth = state.transactions
                    .filter {
                        it.date.year == now.year && it.date.monthValue == now.monthValue &&
                                it.type == TransactionType.EXPENSE
                    }
                    .fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
                val ratio = (spentThisMonth.toDouble() / limitValue.toDouble())
                    .coerceIn(0.0, 1.0).toFloat()
                val over = spentThisMonth > limitValue
                val barColor = if (over) FinanceColors.Expense else FinanceColors.Income
                FinanceCard {
                    Text("Лимит на месяц", color = FinanceColors.TextSecondary, fontSize = 12.sp)
                    Text(
                        "${formatMoney(spentThisMonth)} из ${formatMoney(limitValue)} Р",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = barColor,
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = barColor,
                        trackColor = FinanceColors.Divider,
                    )
                    if (over) {
                        Spacer(Modifier.height(4.dp))
                        Text("Лимит превышен", color = FinanceColors.Expense, fontSize = 12.sp)
                    }
                }
            }

            if (id != null) {
                FinanceCard {
                    Text("Всего потрачено", color = FinanceColors.TextSecondary, fontSize = 12.sp)
                    Text("${formatMoney(state.totalSpent)} Р",
                        fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        color = FinanceColors.Expense)
                }
                SecondaryButton(
                    text = "Изменить транзакции",
                    onClick = { navController.navigate(FinanceRoutes.AssignTagTransactions.build(id)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (id != null) {
            Text(
                "Транзакции этого месяца",
                color = FinanceColors.TextSecondary, fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            val now = LocalDate.now()
            val monthTxs = state.transactions.filter {
                it.date.year == now.year && it.date.monthValue == now.monthValue
            }
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                if (monthTxs.isEmpty()) {
                    item {
                        Text("Нет транзакций в текущем месяце.",
                            color = FinanceColors.TextSecondary, fontSize = 13.sp)
                    }
                } else {
                    items(monthTxs, key = { it.id }) { tx -> TxLine(tx) }
                }
            }
        }

        state.error?.let { Text(it, color = FinanceColors.Expense, modifier = Modifier.padding(16.dp)) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (id != null) {
                SecondaryButton(text = "Удалить",
                    onClick = { vm.onEvent(TagEditEvent.Delete) },
                    modifier = Modifier.weight(1f))
            }
            PrimaryButton(text = "Сохранить",
                onClick = { vm.onEvent(TagEditEvent.Save) },
                modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TxLine(tx: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(FinanceColors.CardBackground)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(tx.name, fontSize = 14.sp, color = FinanceColors.TextPrimary,
                fontWeight = FontWeight.SemiBold)
            Text(tx.date.format(DateTimeFormatter.ofPattern("d MMM")),
                fontSize = 11.sp, color = FinanceColors.TextSecondary)
        }
        Text("− ${formatMoney(tx.amount)} Р", color = FinanceColors.Expense,
            fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
