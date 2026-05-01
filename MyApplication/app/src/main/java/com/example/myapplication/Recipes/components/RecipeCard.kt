package com.example.myapplication.Recipes.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.domain.Recipes.model.Recipe
import com.example.myapplication.R

@Composable
fun RecipeCard(
    recipe: Recipe,
    onFavoriteClick: () -> Unit,
    onLikeClick: () -> Unit,
    onRecipeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onRecipeClick() }
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            if (!recipe.previewImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = recipe.previewImageUrl,
                    contentDescription = recipe.title,
                    error = painterResource(R.drawable.food),
                    placeholder = painterResource(R.drawable.loading),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.img_3),
                    contentDescription = "No image",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_av_timer_24),
                        contentDescription = "Время",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.cookingTimeMinutes} мин",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = recipe.title,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(4.dp))



        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onLikeClick() }
            ) {
                Icon(
                    imageVector = if (recipe.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Лайк",
                    tint = if (recipe.isLiked) Color.Red else Color.Gray,
                    modifier = Modifier.size(if (recipe.isLiked) 24.dp else 20.dp)
                )
                Text(
                    text = recipe.likesCount.toString(),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (recipe.isFavorite) R.drawable.bookmark_filled
                        else R.drawable.favourite
                    ),
                    contentDescription = "В избранное",
                    tint = if (recipe.isFavorite) Color(0xFFE4DB40) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}