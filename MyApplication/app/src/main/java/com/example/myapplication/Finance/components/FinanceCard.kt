package com.example.myapplication.Finance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.myapplication.Finance.theme.FinanceColors

@Composable
fun FinanceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val base = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .background(FinanceColors.CardBackground)
        .border(1.dp, FinanceColors.CardStroke, RoundedCornerShape(20.dp))
    val full = if (onClick != null) base.clickable(onClick = onClick) else base
    Column(
        modifier = modifier.then(full).padding(16.dp),
        content = content,
    )
}