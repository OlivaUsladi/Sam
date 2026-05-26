package com.example.myapplication.Finance.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object FinanceColors {
    val Background = Color(0xFFF6F6F6)
    val CardBackground = Color.White
    val CardStroke = Color(0xFFEDEDED)

    val HeaderBackground = Color(0xFF1B2440)
    val HeaderText = Color.White
    val HeaderAccentDot = Color(0xFFFFA726)

    val AccentSolid = Color(0xFFFF8A1A)
    val AccentDark = Color(0xFFE65100)
    val AccentSoft = Color(0xFFFFF1E0)
    val AccentGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFFFC042), Color(0xFFFF6F00))
    )

    val TipBackground = Color(0xFFDDE9FB)
    val TipBorder = Color(0xFFB7CBEC)
    val TipText = Color(0xFF1B5BB8)

    val TextPrimary = Color(0xFF131A29)
    val TextSecondary = Color(0xFF7A7A7A)
    val TextOnAccent = Color.White
    val Divider = Color(0xFFE6E6E6)

    val Income = Color(0xFF2E7D32)
    val Expense = Color(0xFFD24A4A)

    val SecondaryButton = Color(0xFFE2E2E2)
    val SecondaryButtonText = Color(0xFF131A29)

    val ChartPalette = listOf(
        Color(0xFFFF8A1A),
        Color(0xFFFFC042),
        Color(0xFF1B2440),
        Color(0xFF2670CC),
        Color(0xFF26C6DA),
        Color(0xFF66BB6A),
        Color(0xFFAB47BC),
        Color(0xFFEF5350),
        Color(0xFF8D6E63),
    )

    val Primary = AccentSolid
    val PrimaryDark = AccentDark
    val PrimarySoft = AccentSoft
    val HeaderGradient = AccentGradient
}
