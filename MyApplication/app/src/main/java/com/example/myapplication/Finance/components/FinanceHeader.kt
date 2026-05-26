package com.example.myapplication.Finance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.Finance.theme.FinanceColors

@Composable
fun FinanceHeader(
    title: String,
    subtitle: String? = null,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onAction: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(FinanceColors.Background)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    title, color = FinanceColors.TextPrimary,
                    fontSize = 24.sp, fontWeight = FontWeight.Bold,
                )
                if (subtitle != null) {
                    Text(subtitle, color = FinanceColors.TextSecondary, fontSize = 13.sp)
                }
            }
            if (actionIcon != null && onAction != null) {
                IconButton(onClick = onAction) {
                    Icon(actionIcon, contentDescription = null, tint = FinanceColors.AccentSolid)
                }
            }
        }
    }
}