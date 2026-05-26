package com.example.myapplication.Finance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.Finance.theme.FinanceColors

@Composable
fun AccountDropdown(
    items: List<Pair<Int?, String>>,
    selectedId: Int?,
    onSelect: (Int?) -> Unit,
    placeholder: String = "Выбрать счёт",
    modifier: Modifier = Modifier,
) {
    var open by remember { mutableStateOf(false) }
    val currentLabel = items.firstOrNull { it.first == selectedId }?.second ?: placeholder
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(FinanceColors.CardBackground)
                .border(1.dp, FinanceColors.CardStroke, RoundedCornerShape(20.dp))
                .clickable { open = true }
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                currentLabel,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                color = if (selectedId == null) FinanceColors.TextSecondary else FinanceColors.TextPrimary,
            )
            Icon(
                Icons.Default.ExpandMore,
                contentDescription = null,
                tint = FinanceColors.AccentSolid,
            )
        }
        DropdownMenu(
            expanded = open,
            onDismissRequest = { open = false },
        ) {
            items.forEach { (id, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelect(id)
                        open = false
                    },
                )
            }
        }
    }
}