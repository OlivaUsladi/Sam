package com.example.myapplication.Recipes.ui.categoryrecipes

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.Recipes.components.RecipeCard
import com.example.myapplication.Recipes.components.RecipeSearchBar
import com.example.myapplication.Recipes.navigation.Routes
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CategoryRecipesScreen(
    navController: NavController,
    categoryId: Int,
    categoryName: String,
    viewModel: CategoryRecipesViewModel = koinViewModel(
        parameters = { parametersOf(categoryId, categoryName) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFFE4DB40), Color(0xFF3AC42A))
        )
    }

    LaunchedEffect(categoryId) {
        viewModel.onEvent(CategoryRecipesEvent.LoadRecipes(categoryId))
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
            Box(
                modifier = Modifier
                    .clickable { navController.navigateUp() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Назад",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = categoryName,
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
            onQueryChange = { viewModel.onEvent(CategoryRecipesEvent.SearchQueryChanged(it)) },
            onSearch = { viewModel.onEvent(CategoryRecipesEvent.Search(it)) },
            suggestions = uiState.searchSuggestions,
            showSuggestions = uiState.showSuggestions,
            onSuggestionClick = { viewModel.onEvent(CategoryRecipesEvent.Search(it)) },
            onClearClick = { viewModel.onEvent(CategoryRecipesEvent.ClearSearch) },
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
                                viewModel.onEvent(CategoryRecipesEvent.ToggleFavorite(recipe.id))
                            },
                            onLikeClick = {
                                viewModel.onEvent(CategoryRecipesEvent.ToggleLike(recipe.id))
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