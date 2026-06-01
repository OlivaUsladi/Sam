package com.example.myapplication.Finance.ui.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.Finance.model.Transaction
import com.example.myapplication.Finance.components.*
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun AssignTagTransactionsScreen(
    navController: NavController,
    tagId: Int,
    vm: AssignTagTransactionsViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(tagId) { vm.onEvent(AssignTagEvent.Init(tagId)) }
    LaunchedEffect(state.saved) { if (state.saved) navController.navigateUp() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(
            title = "Транзакции",
            subtitle = state.tagName.ifBlank { "Прикрепить к тегу" },
            actionIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onAction = { navController.navigateUp() },
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FinanceColors.PrimaryDark)
            }
        }
        else {

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (state.expenses.isEmpty()) {
                        item {
                            Text(
                                "Расходов ещё нет.",
                                color = FinanceColors.TextSecondary, fontSize = 13.sp
                            )
                        }
                    } else {
                        items(state.expenses, key = { it.id }) { tx ->
                            TxRow(
                                tx = tx,
                                selected = state.selected.contains(tx.id),
                                onToggle = { vm.onEvent(AssignTagEvent.Toggle(tx.id)) },
                            )
                        }
                    }
                }
            }


            state.error?.let {
                Text(
                    it,
                    color = FinanceColors.Expense,
                    modifier = Modifier.padding(16.dp)
                )
            }

            PrimaryButton(
                text = "Сохранить",
                onClick = { vm.onEvent(AssignTagEvent.Save) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
        }
    }
}

@Composable
private fun TxRow(tx: Transaction, selected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(FinanceColors.CardBackground)
            .clickable(onClick = onToggle)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (selected) FinanceColors.Primary else Color.Transparent)
                .border(1.dp, FinanceColors.PrimaryDark, RoundedCornerShape(11.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(tx.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                color = FinanceColors.TextPrimary)
            Text(tx.date.format(DateTimeFormatter.ofPattern("d MMM")),
                fontSize = 11.sp, color = FinanceColors.TextSecondary)
        }
        Text("− ${formatMoney(tx.amount)} Р", color = FinanceColors.Expense,
            fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
