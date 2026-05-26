package com.example.myapplication.Finance.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalEditScreen(
    navController: NavController,
    id: Int?,
    vm: GoalEditViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(id) { vm.onEvent(GoalEditEvent.Init(id)) }
    LaunchedEffect(state.saved) { if (state.saved) navController.navigateUp() }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.targetDate
            ?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(
            title = if (id == null) "Новая цель" else "Изменить цель",
            subtitle = "Накопления",
            actionIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onAction = { navController.navigateUp() },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FinanceTextField(value = state.name,
                onValueChange = { vm.onEvent(GoalEditEvent.SetName(it)) },
                label = "Название")
            FinanceTextField(value = state.description,
                onValueChange = { vm.onEvent(GoalEditEvent.SetDescription(it)) },
                label = "Описание (необязательно)",
                singleLine = false, maxLines = 4)
            FinanceTextField(value = state.targetAmount,
                onValueChange = { vm.onEvent(GoalEditEvent.SetTarget(it)) },
                label = "Целевая сумма, Р",
                keyboardType = KeyboardType.Decimal)
            if (id != null) {
                FinanceTextField(value = state.currentAmount,
                    onValueChange = { vm.onEvent(GoalEditEvent.SetCurrent(it)) },
                    label = "Уже накоплено, Р",
                    keyboardType = KeyboardType.Decimal)
            }
            FinanceTextField(value = state.monthlyAmount,
                onValueChange = { vm.onEvent(GoalEditEvent.SetMonthly(it)) },
                label = "Ежемесячное внесение (опционально)",
                keyboardType = KeyboardType.Decimal)

            // Дата
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(FinanceColors.CardBackground)
                    .clickable { showDatePicker = true }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null,
                    tint = FinanceColors.PrimaryDark)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("До какого числа", color = FinanceColors.TextSecondary, fontSize = 12.sp)
                    Text(
                        state.targetDate?.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
                            ?: "Без даты",
                        color = FinanceColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (state.targetDate != null) {
                    TextButton(onClick = { vm.onEvent(GoalEditEvent.SetDate(null)) }) {
                        Text("Сброс", color = FinanceColors.TextSecondary)
                    }
                }
            }

            state.error?.let { Text(it, color = FinanceColors.Expense, fontSize = 13.sp) }

            PrimaryButton(text = "Сохранить",
                onClick = { vm.onEvent(GoalEditEvent.Save) },
                modifier = Modifier.fillMaxWidth())
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    val date = millis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    vm.onEvent(GoalEditEvent.SetDate(date))
                    showDatePicker = false
                }) { Text("Ок") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            },
        ) { DatePicker(state = datePickerState) }
    }
}
