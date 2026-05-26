package com.example.myapplication.Finance.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.Finance.model.Source
import com.example.domain.Finance.model.Tag
import com.example.domain.Finance.model.Transaction
import com.example.domain.Finance.model.TransactionType
import com.example.myapplication.Finance.components.*
import com.example.myapplication.Finance.navigation.FinanceRoutes
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    navController: NavController,
    sourceId: Int? = null,
    vm: HistoryViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val sourceById = remember(state.sources) { state.sources.associateBy { it.id } }
    val tagById = remember(state.tags) { state.tags.associateBy { it.id } }

    androidx.compose.runtime.LaunchedEffect(sourceId) {
        vm.onEvent(HistoryEvent.SetSourceFilter(sourceId))
    }

    val sourceName = sourceId?.let { sourceById[it]?.name }
    val subtitle = sourceName?.let { "По счёту: $it" }
        ?: "Все доходы и расходы"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(
            title = "Все транзакции",
            subtitle = subtitle,
        )
        QuickActionsRow(navController = navController)
        FilterRow(
            filter = state.filter,
            onChange = { vm.onEvent(HistoryEvent.ChangeFilter(it)) },
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FinanceColors.PrimaryDark)
            }
        } else if (state.error != null) {
            Text(
                state.error!!,
                color = FinanceColors.Expense,
                modifier = Modifier.padding(16.dp),
            )
        } else if (state.transactions.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.transactions, key = { it.id }) { tx ->
                    TransactionCard(
                        tx = tx,
                        source = sourceById[tx.sourceId],
                        tag = tx.tagId?.let { tagById[it] },
                        onClick = { navController.navigate(FinanceRoutes.EditTx.build(tx.id)) },
                        onDelete = { vm.onEvent(HistoryEvent.Delete(tx.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        QuickAction(
            label = "Источники",
            icon = Icons.Default.AccountBalance,
            modifier = Modifier.weight(1f),
        ) { navController.navigate(FinanceRoutes.Sources.route) }
        QuickAction(
            label = "Добавить",
            icon = Icons.Default.Add,
            modifier = Modifier.weight(1f),
        ) { navController.navigate(FinanceRoutes.NewTx.route) }
        QuickAction(
            label = "Загрузить",
            icon = Icons.Default.PictureAsPdf,
            modifier = Modifier.weight(1f),
        ) { navController.navigate(FinanceRoutes.BankReports.route) }
    }
}

@Composable
private fun QuickAction(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(FinanceColors.CardBackground)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(FinanceColors.PrimarySoft),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, contentDescription = null, tint = FinanceColors.PrimaryDark) }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 12.sp, color = FinanceColors.TextPrimary,
            fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun FilterRow(
    filter: HistoryFilter,
    onChange: (HistoryFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SegmentButton("Все", selected = filter == HistoryFilter.ALL,
            onClick = { onChange(HistoryFilter.ALL) }, modifier = Modifier.weight(1f))
        SegmentButton("Доходы", selected = filter == HistoryFilter.INCOME,
            onClick = { onChange(HistoryFilter.INCOME) }, modifier = Modifier.weight(1f))
        SegmentButton("Расходы", selected = filter == HistoryFilter.EXPENSE,
            onClick = { onChange(HistoryFilter.EXPENSE) }, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TransactionCard(
    tx: Transaction,
    source: Source?,
    tag: Tag?,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val isIncome = tx.type == TransactionType.INCOME
    FinanceCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(tx.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                    color = FinanceColors.TextPrimary)
                Spacer(Modifier.height(2.dp))
                Row {
                    Text(
                        text = tx.date.format(DateTimeFormatter.ofPattern("d MMM")),
                        color = FinanceColors.TextSecondary, fontSize = 12.sp,
                    )
                    if (source != null) {
                        Text(" · ${source.name}", color = FinanceColors.TextSecondary, fontSize = 12.sp)
                    }
                    if (tag != null) {
                        Text(" · ${tag.name}", color = FinanceColors.PrimaryDark, fontSize = 12.sp)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatMoneySigned(tx.amount, income = isIncome),
                    color = if (isIncome) FinanceColors.Income else FinanceColors.Expense,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить",
                        tint = FinanceColors.TextSecondary)
                }
            }
        }
        if (!tx.description.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(tx.description ?: "", color = FinanceColors.TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Транзакций пока нет",
            color = FinanceColors.TextSecondary,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Добавьте доход или расход — он появится в этом списке.",
            color = FinanceColors.TextSecondary, fontSize = 12.sp,
        )
    }
}

