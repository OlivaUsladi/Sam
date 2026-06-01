package com.example.myapplication.Finance.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.Finance.components.AccountDropdown
import com.example.myapplication.Finance.components.FinanceCard
import com.example.myapplication.Finance.components.GreyButton
import com.example.myapplication.Finance.components.TipBanner
import com.example.myapplication.Finance.navigation.FinanceRoutes
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun AccountsScreen(
    navController: NavController,
    vm: AccountsViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val dropdownItems = remember(state.sources) {
        listOf<Pair<Int?, String>>(null to "Все счета") +
            state.sources.map { it.id as Int? to it.name }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        //Вот тут пока непонятно, успею добавить или нет
//        TipBanner(
//            text = "Привет! Здесь может быть какой-то важный совет от системы " +
//                "приложения, например что налоги надо платить вовремя :)"
//        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Счета",
                color = FinanceColors.TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Добавляйте новые транзакции вручную или загрузите отчёт по нужному счёту",
                color = FinanceColors.TextSecondary,
                fontSize = 14.sp,
            )
        }

        AccountDropdown(
            items = dropdownItems,
            selectedId = state.selectedSourceId,
            onSelect = { vm.selectSource(it) },
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FinanceColors.AccentDark)
            }
        }
        state.error?.let { err ->
            FinanceCard {
                Text(err, color = FinanceColors.Expense, fontSize = 13.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AccentCard(
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(FinanceRoutes.NewTx.route) }
            )
            AllTransactionsCard(
                modifier = Modifier.weight(1f),
                onClick = {
                    val sid = state.selectedSourceId
                    val route = if (sid != null) FinanceRoutes.History.buildForSource(sid)
                                else FinanceRoutes.History.route
                    navController.navigate(route)
                }
            )
        }

        UploadReportCard(
            onClick = { navController.navigate(FinanceRoutes.BankReports.route) }
        )

        GreyButton(
            text = "+ Добавить счёт",
            onClick = { navController.navigate(FinanceRoutes.Sources.route) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AccentCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(FinanceColors.AccentGradient)
            .clickable(onClick = onClick)
            .padding(18.dp),
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Text("/", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White)
        }
        Text(
            "Добавить",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.BottomStart),
        )
    }
}

@Composable
private fun AllTransactionsCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(FinanceColors.CardBackground)
            .border(1.dp, FinanceColors.CardStroke, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
    ) {
        Text(
            "Все\nтранзакции",
            color = FinanceColors.AccentDark,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.BottomStart),
        )
    }
}

@Composable
private fun UploadReportCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FinanceColors.CardBackground)
            .border(1.dp, FinanceColors.CardStroke, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.PictureAsPdf,
            contentDescription = null,
            tint = FinanceColors.AccentSolid,
            modifier = Modifier.size(32.dp),
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                "Загрузить отчёт",
                color = FinanceColors.TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "История транзакций добавится автоматически",
                color = FinanceColors.TextSecondary,
                fontSize = 13.sp,
            )
        }
    }
}
