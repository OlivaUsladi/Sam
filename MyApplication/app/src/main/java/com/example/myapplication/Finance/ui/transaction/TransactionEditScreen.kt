package com.example.myapplication.Finance.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.domain.Finance.model.TransactionType
import com.example.myapplication.Finance.components.*
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun TransactionEditScreen(
    navController: NavController,
    id: Int?,
    vm: TransactionEditViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(id) { vm.onEvent(TransactionEditEvent.Init(id)) }
    LaunchedEffect(state.saved) {
        if (state.saved) navController.navigateUp()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(
            title = if (id == null) "Новая транзакция" else "Изменить транзакцию",
            subtitle = state.date.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
            actionIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onAction = { navController.navigateUp() },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Тип: доход/расход
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SegmentButton(
                    "Расход",
                    selected = state.type == TransactionType.EXPENSE,
                    onClick = { vm.onEvent(TransactionEditEvent.SetType(TransactionType.EXPENSE)) },
                    modifier = Modifier.weight(1f),
                )
                SegmentButton(
                    "Доход",
                    selected = state.type == TransactionType.INCOME,
                    onClick = { vm.onEvent(TransactionEditEvent.SetType(TransactionType.INCOME)) },
                    modifier = Modifier.weight(1f),
                )
            }

            FinanceTextField(
                value = state.name,
                onValueChange = { vm.onEvent(TransactionEditEvent.SetName(it)) },
                label = "Название",
            )
            FinanceTextField(
                value = state.amount,
                onValueChange = { vm.onEvent(TransactionEditEvent.SetAmount(it)) },
                label = "Сумма, ₽",
                keyboardType = KeyboardType.Decimal,
            )
            FinanceTextField(
                value = state.description,
                onValueChange = { vm.onEvent(TransactionEditEvent.SetDescription(it)) },
                label = "Описание (необязательно)",
                singleLine = false,
                maxLines = 4,
            )

            // Источник
            Text("Источник", color = FinanceColors.TextSecondary, fontSize = 12.sp)
            if (state.sources.isEmpty()) {
                Text(
                    "Сначала добавьте источник в разделе «Источники».",
                    color = FinanceColors.Expense, fontSize = 12.sp,
                )
            } else {
                ChipFlow(
                    items = state.sources.map { it.id to it.name },
                    selectedId = state.sourceId,
                    onClick = { vm.onEvent(TransactionEditEvent.SetSource(it)) },
                )
            }

            // Тэг
            if (state.type == TransactionType.EXPENSE) {
                Text("Тэг (необязательно)", color = FinanceColors.TextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SegmentButton(
                        "Без тэга",
                        selected = state.tagId == null,
                        onClick = { vm.onEvent(TransactionEditEvent.SetTag(null)) },
                    )
                }
                ChipFlow(
                    items = state.tags.map { it.id to it.name },
                    selectedId = state.tagId,
                    onClick = { vm.onEvent(TransactionEditEvent.SetTag(it)) },
                )
            }

            state.error?.let {
                Text(it, color = FinanceColors.Expense, fontSize = 13.sp)
            }

            PrimaryButton(
                text = "Сохранить",
                onClick = { vm.onEvent(TransactionEditEvent.Save) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipFlow(
    items: List<Pair<Int, String>>,
    selectedId: Int?,
    onClick: (Int) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items.forEach { (id, name) ->
            val selected = id == selectedId
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) FinanceColors.Primary else Color.White)
                    .clickable { onClick(id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    name,
                    fontSize = 13.sp,
                    color = if (selected) FinanceColors.TextPrimary else FinanceColors.TextSecondary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}
