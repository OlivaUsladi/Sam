package com.example.myapplication.Finance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.Finance.theme.FinanceColors
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = FinanceColors.AccentSolid,
            contentColor = FinanceColors.TextOnAccent,
            disabledContainerColor = FinanceColors.AccentSolid.copy(alpha = 0.4f),
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .heightIn(min = 48.dp),
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
fun GreyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = FinanceColors.SecondaryButton,
            contentColor = FinanceColors.SecondaryButtonText,
            disabledContainerColor = FinanceColors.SecondaryButton.copy(alpha = 0.5f),
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.heightIn(min = 52.dp),
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = FinanceColors.TextPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, FinanceColors.AccentDark),
        modifier = modifier.heightIn(min = 48.dp),
    ) {
        Text(text, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun SegmentButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (selected) FinanceColors.AccentSolid else Color.White
    val border = if (selected) FinanceColors.AccentDark else FinanceColors.Divider
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text,
            fontSize = 13.sp,
            color = if (selected) FinanceColors.TextPrimary else FinanceColors.TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
fun AddRoundButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(FinanceColors.AccentGradient)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Default.Add, contentDescription = "Добавить", tint = Color.White)
    }
}

private val moneyFormat: DecimalFormat = DecimalFormat(
    "#,##0.00", DecimalFormatSymbols(Locale("ru", "RU")).apply {
        groupingSeparator = ' '; decimalSeparator = ','
    }
)

fun formatMoney(amount: BigDecimal): String = moneyFormat.format(amount)

fun formatMoneySigned(amount: BigDecimal, income: Boolean): String =
    (if (income) "+" else "−") + " " + moneyFormat.format(amount.abs()) + " Р"
