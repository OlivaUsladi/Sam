package com.example.myapplication.Recipes.ui.favourite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.Recipes.components.RecipeCard
import com.example.myapplication.Recipes.components.RecipeSearchBar
import com.example.myapplication.Recipes.navigation.Routes
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavouriteRecipeScreen(
    navController: NavController,
    viewModel: FavouriteRecipeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.onEvent(FavouriteEvent.LoadFavourites)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp)
        ) {
            Text(
                text = "Избранное",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        RecipeSearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onEvent(FavouriteEvent.SearchQueryChanged(it)) },
            onSearch = {
                focusManager.clearFocus()
                viewModel.onEvent(FavouriteEvent.Search(it))
            },
            suggestions = uiState.searchSuggestions,
            showSuggestions = uiState.showSuggestions,
            onSuggestionClick = {
                focusManager.clearFocus()
                viewModel.onEvent(FavouriteEvent.Search(it))
            },
            onClearClick = { viewModel.onEvent(FavouriteEvent.ClearSearch) },
            modifier = Modifier.fillMaxWidth()
        )

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

            uiState.recipes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.bookmark),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "В избранном пока пусто",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Добавляйте рецепты в избранное,\nчтобы они появлялись здесь",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                LazyColumn {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(
                        items = uiState.recipes,
                        key = { it.id }
                    ) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onFavoriteClick = {
                                viewModel.onEvent(FavouriteEvent.ToggleFavorite(recipe.id))
                            },
                            onLikeClick = {
                                viewModel.onEvent(FavouriteEvent.ToggleLike(recipe.id))
                            },
                            onRecipeClick = {
                                navController.navigate("${Routes.Recipe.route}/${recipe.id}")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}