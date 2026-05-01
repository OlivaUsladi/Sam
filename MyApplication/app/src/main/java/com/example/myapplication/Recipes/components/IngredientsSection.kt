package com.example.myapplication.Recipes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.Recipes.model.RecipeIngredient

@Composable
fun IngredientsSection(
    ingredients: List<RecipeIngredient>,
    onAddToShoppingList: () -> Unit
) {
    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFF3AC42A), Color(0xFFDAD23B))
        )
    }
    Column( horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .background(Color.White)
        .border(1.dp, SolidColor(Color(0xFFF45B03)),shape = RoundedCornerShape(25.dp))
    )  {
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Ингредиенты",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF25232B),
            modifier = Modifier.padding(start = 30.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        ingredients.forEach { ingredient ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = ingredient.groceryItem.name,
                    fontSize = 20.sp,
                    color = Color(0xFF25232B),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${ingredient.amount} ${ingredient.unit}",
                    fontSize = 20.sp,
                    color = Color(0xFF25232B)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(brush)
                .clickable { onAddToShoppingList() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Добавить в список",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}