package com.example.myapplication.Recipes.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.domain.Recipes.model.ContentBlock

@Composable
fun CookingStepsSection(steps: List<ContentBlock>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Приготовление",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        steps.forEachIndexed { index, step ->
            when (step) {
                is ContentBlock.Paragraph -> {
                    Text(
                        text = step.text,
                        fontSize = step.size.sp,
                        color = Color.Black,
                        lineHeight = 35.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                is ContentBlock.Image -> {
                    AsyncImage(
                        model = step.url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}