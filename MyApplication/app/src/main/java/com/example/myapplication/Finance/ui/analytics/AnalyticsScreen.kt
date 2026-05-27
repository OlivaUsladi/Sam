package com.example.myapplication.Finance.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.Finance.model.DailyTotal
import com.example.domain.Finance.model.SourceBucket
import com.example.domain.Finance.model.TransactionType
import com.example.myapplication.Finance.components.*
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AnalyticsScreen(
    navController: NavController,
    vm: AnalyticsViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(
            title = "Аналитика",
            subtitle = "Доходы и расходы по месяцам",
        )

        // Переключатель месяца
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(onClick = { vm.onEvent(AnalyticsEvent.PrevMonth) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Предыдущий месяц",
                    tint = FinanceColors.PrimaryDark)
            }
            Text(
                text = "${state.month.month.getDisplayName(TextStyle.FULL_STANDALONE, 
                    Locale("ru"))} ${state.month.year}".replaceFirstChar { it.uppercase() },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = FinanceColors.TextPrimary,
            )
            IconButton(onClick = { vm.onEvent(AnalyticsEvent.NextMonth) }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Следующий месяц",
                    tint = FinanceColors.PrimaryDark)
            }
        }

        // Тип
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SegmentButton(
                "Расходы",
                selected = state.type == TransactionType.EXPENSE,
                onClick = { vm.onEvent(AnalyticsEvent.SetType(TransactionType.EXPENSE)) },
                modifier = Modifier.weight(1f),
            )
            SegmentButton(
                "Доходы",
                selected = state.type == TransactionType.INCOME,
                onClick = { vm.onEvent(AnalyticsEvent.SetType(TransactionType.INCOME)) },
                modifier = Modifier.weight(1f),
            )
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FinanceColors.PrimaryDark)
            }
            return@Column
        }

        state.error?.let { Text(it, color = FinanceColors.Expense, modifier = Modifier.padding(16.dp)) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val data = state.data
            // Итог
            FinanceCard {
                Text("Всего за месяц", color = FinanceColors.TextSecondary, fontSize = 12.sp)
                Text(
                    text = formatMoney(data?.total ?: BigDecimal.ZERO) + " Р",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (state.type == TransactionType.INCOME)
                        FinanceColors.Income else FinanceColors.Expense,
                )
            }

            // Столбчатая
            FinanceCard {
                Text(
                    if (state.type == TransactionType.INCOME) "Доходы по дням"
                    else "Расходы по дням",
                    fontWeight = FontWeight.SemiBold,
                    color = FinanceColors.TextPrimary,
                )
                Spacer(Modifier.height(8.dp))
                BarChart(daily = data?.daily.orEmpty())
            }

            // Круговая
            FinanceCard {
                Text(
                    "По источникам",
                    fontWeight = FontWeight.SemiBold,
                    color = FinanceColors.TextPrimary,
                )
                Spacer(Modifier.height(8.dp))
                PieWithLegend(buckets = data?.bySource.orEmpty())
            }
        }
    }
}


@Composable
private fun BarChart(daily: List<DailyTotal>) {
    if (daily.isEmpty()) {
        Text("Нет данных за этот месяц",
            color = FinanceColors.TextSecondary, fontSize = 12.sp)
        return
    }
    val max = daily.maxOf { it.amount.toDouble() }.coerceAtLeast(1.0)
    val barTopColor = FinanceColors.AccentSolid
    val barBottomColor = FinanceColors.AccentDark
    val gridColor = FinanceColors.Divider


    val xLabelPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#7A7A7A")
            textSize = 24f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }

    val yLabelPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#7A7A7A")
            textSize = 24f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.RIGHT
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        val xLabelStripH = 32f
        val yAxisStripW = 70f
        val chartHeight = size.height - xLabelStripH
        val chartLeft   = yAxisStripW
        val chartWidth  = size.width - yAxisStripW


        val steps = 4
        for (i in 0..steps) {
            val y = chartHeight * i / steps.toFloat()
            drawLine(
                color = gridColor,
                start = Offset(chartLeft, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )

            val value = max * (1.0 - i.toDouble() / steps)
            drawContext.canvas.nativeCanvas.drawText(
                shortRubLabel(value),
                chartLeft - 6f,
                y + 8f,
                yLabelPaint,
            )
        }

        val n = daily.size
        val slotW = chartWidth / n
        val barW = slotW * 0.55f
        val barOffset = (slotW - barW) / 2
        daily.forEachIndexed { i, d ->
            val ratio = (d.amount.toDouble() / max).toFloat().coerceIn(0f, 1f)
            val h = chartHeight * ratio
            val left = chartLeft + i * slotW + barOffset
            drawRoundRect(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(barTopColor, barBottomColor)
                ),
                topLeft = Offset(left, chartHeight - h),
                size = Size(barW, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
            )
            val day = d.date.dayOfMonth
            if (day == 1 || day % 5 == 0 || i == n - 1) {
                drawContext.canvas.nativeCanvas.drawText(
                    day.toString(),
                    chartLeft + i * slotW + slotW / 2,
                    size.height - 6f,
                    xLabelPaint,
                )
            }
        }
    }
}


private fun shortRubLabel(v: Double): String = when {
    v >= 1_000_000 -> "%.1fмР".format(v / 1_000_000).replace(',', '.')
    v >= 1_000     -> "%.1fкР".format(v / 1_000).replace(',', '.')
    v >= 1         -> "${v.toInt()}Р"
    else           -> "0"
}

@Composable
private fun PieWithLegend(buckets: List<SourceBucket>) {
    if (buckets.isEmpty()) {
        Text("Нет данных по источникам",
            color = FinanceColors.TextSecondary, fontSize = 12.sp)
        return
    }
    val total = buckets.sumOf { it.amount.toDouble() }.coerceAtLeast(1.0)
    val palette = FinanceColors.ChartPalette

    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(140.dp)) {
            var start = -90f
            buckets.forEachIndexed { i, b ->
                val sweep = (b.amount.toDouble() / total * 360.0).toFloat()
                drawArc(
                    color = palette[i % palette.size],
                    startAngle = start,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset.Zero,
                    size = size,
                )
                start += sweep
            }
            drawCircle(color = Color.White, radius = size.minDimension / 3.2f)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            buckets.forEachIndexed { i, b ->
                val pct = (b.amount.toDouble() / total * 100).toInt()
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(palette[i % palette.size]),
                    )
                    Spacer(Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(b.sourceName, fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FinanceColors.TextPrimary)
                        Text("${formatMoney(b.amount)} Р · $pct%",
                            fontSize = 11.sp,
                            color = FinanceColors.TextSecondary)
                    }
                }
            }
        }
    }
}
