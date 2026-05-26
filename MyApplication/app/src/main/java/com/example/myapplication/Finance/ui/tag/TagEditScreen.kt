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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.Finance.model.Transaction
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
            title = if (id == null) "Новый тэг" else "Изменить тэг",
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
                label = "Название тэга")

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
