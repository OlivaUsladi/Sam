package com.example.myapplication.Finance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.Finance.theme.FinanceColors

//Если всё-таки будет связь между модулями или просто обучающие динамические подсказки
@Composable
fun TipBanner(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(FinanceColors.TipBackground)
            .border(1.dp, FinanceColors.TipBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(
            text,
            color = FinanceColors.TipText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}