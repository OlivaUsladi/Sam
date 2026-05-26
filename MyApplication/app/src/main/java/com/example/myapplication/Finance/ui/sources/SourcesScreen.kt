package com.example.myapplication.Finance.ui.sources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
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
import com.example.domain.Finance.model.Source
import com.example.domain.Finance.model.SourceType
import com.example.myapplication.Finance.components.*
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun SourcesScreen(
    navController: NavController,
    vm: SourcesViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(
            title = "Источники",
            subtitle = "Банки, карты и наличные",
            actionIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onAction = { navController.navigateUp() },
        )

        Box(modifier = Modifier.weight(1f)) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = FinanceColors.PrimaryDark)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.sources, key = { it.id }) { src ->
                        SourceCard(
                            src = src,
                            onEdit = { vm.onEvent(SourcesEvent.OpenEdit(src)) },
                            onDelete = { vm.onEvent(SourcesEvent.Delete(src.id)) },
                        )
                    }
                    if (state.sources.isEmpty() && !state.isLoading) {
                        item {
                            Text(
                                "Ещё нет источников. Нажмите «+» внизу.",
                                color = FinanceColors.TextSecondary,
                                modifier = Modifier.padding(top = 24.dp),
                            )
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = { vm.onEvent(SourcesEvent.OpenCreate) },
                containerColor = FinanceColors.Primary,
                contentColor = FinanceColors.TextPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
            ) { Icon(Icons.Default.Add, contentDescription = "Добавить") }
        }

        state.error?.let {
            Text(it, color = FinanceColors.Expense, modifier = Modifier.padding(16.dp))
        }
    }

    if (state.showCreateDialog) {
        SourceDialog(
            initial = state.editing,
            onDismiss = { vm.onEvent(SourcesEvent.Dismiss) },
            onSave = { name, type ->
                vm.onEvent(SourcesEvent.Save(state.editing?.id, name, type))
            },
        )
    }
}

@Composable
private fun SourceCard(
    src: Source,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    FinanceCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FinanceColors.PrimarySoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(iconFor(src.type), contentDescription = null, tint = FinanceColors.PrimaryDark)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(src.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                    color = FinanceColors.TextPrimary)
                Text(labelFor(src.type), fontSize = 12.sp, color = FinanceColors.TextSecondary)
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

private fun iconFor(t: SourceType) = when (t) {
    SourceType.BANK -> Icons.Default.AccountBalance
    SourceType.CARD -> Icons.Default.CreditCard
    SourceType.CASH -> Icons.Default.Payments
}

private fun labelFor(t: SourceType) = when (t) {
    SourceType.BANK -> "Банк"
    SourceType.CARD -> "Карта"
    SourceType.CASH -> "Наличные"
}

@Composable
private fun SourceDialog(
    initial: Source?,
    onDismiss: () -> Unit,
    onSave: (String, SourceType) -> Unit,
) {
    var name by remember { mutableStateOf(initial?.name.orEmpty()) }
    var type by remember { mutableStateOf(initial?.type ?: SourceType.BANK) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Новый источник" else "Изменить источник") },
        text = {
            Column {
                FinanceTextField(value = name, onValueChange = { name = it }, label = "Название")
                Spacer(Modifier.height(12.dp))
                Text("Тип", fontSize = 12.sp, color = FinanceColors.TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SegmentButton("Банк",     selected = type == SourceType.BANK,
                        onClick = { type = SourceType.BANK },     modifier = Modifier.weight(1f))
                    SegmentButton("Карта",    selected = type == SourceType.CARD,
                        onClick = { type = SourceType.CARD },     modifier = Modifier.weight(1f))
                    SegmentButton("Наличные", selected = type == SourceType.CASH,
                        onClick = { type = SourceType.CASH },     modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            PrimaryButton(
                text = "Сохранить",
                enabled = name.isNotBlank(),
                onClick = { onSave(name.trim(), type) },
            )
        },
        dismissButton = { SecondaryButton(text = "Отмена", onClick = onDismiss) },
    )
}
