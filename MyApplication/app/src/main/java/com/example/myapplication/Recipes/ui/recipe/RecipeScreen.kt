package com.example.myapplication.Recipes.ui.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.Recipes.components.CookingStepsSection
import com.example.myapplication.Recipes.components.IngredientsSection
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.format.DateTimeFormatter

@Composable
fun RecipeScreen(
    navController: NavController,
    recipeId: Int,
    viewModel: RecipeScreenViewModel = koinViewModel(
        parameters = { parametersOf(recipeId) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFFE4DB40), Color(0xFF3AC42A))
        )
    }

    LaunchedEffect(recipeId) {
        viewModel.onEvent(RecipeDetailEvent.LoadRecipe(recipeId))
        viewModel.onEvent(RecipeDetailEvent.LoadRecipeContent(recipeId))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Назад",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE4DB40))
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ошибка: ${uiState.error}",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }

            uiState.recipe != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(274.dp)
                    ) {
                        if (!uiState.recipe!!.previewImageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = uiState.recipe!!.previewImageUrl,
                                contentDescription = uiState.recipe!!.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.food),
                                contentDescription = uiState.recipe!!.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = uiState.recipe!!.createdAt.format(
                                DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            ),
                            fontSize = 16.sp,
                            color = Color(0xFF878787),
                            modifier = Modifier.align(Alignment.End)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.recipe!!.title,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            lineHeight = 40.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.clickable {
                                    viewModel.onEvent(RecipeDetailEvent.ToggleLike)
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (uiState.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Лайк",
                                    tint = if (uiState.isLiked) Color.Red else Color(0xFF131A29),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.likesCount.toString(),
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(32.dp))

                            Row(
                                modifier = Modifier.clickable {
                                    viewModel.onEvent(RecipeDetailEvent.ToggleFavorite)
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (uiState.isFavorite) R.drawable.bookmark_filled
                                        else R.drawable.favourite
                                    ),
                                    contentDescription = "В избранное",
                                    tint = if (uiState.isFavorite) Color(0xFFE4DB40) else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = buildString {
                                append("Категории: ")
                                append(uiState.recipe!!.categories.joinToString(", ") { it.name })
                                append("\nВремя: ${uiState.recipe!!.cookingTimeMinutes} мин")
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            lineHeight = 35.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        IngredientsSection(
                            ingredients = uiState.ingredients,
                            onAddToShoppingList = {
                                viewModel.onEvent(RecipeDetailEvent.AddToShoppingList)
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (uiState.cookingSteps.isNotEmpty()) {
                            CookingStepsSection(steps = uiState.cookingSteps)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}