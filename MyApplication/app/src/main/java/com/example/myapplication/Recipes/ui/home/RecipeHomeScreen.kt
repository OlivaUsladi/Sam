package com.example.myapplication.Recipes.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.Recipes.components.RecipeCard
import com.example.myapplication.Recipes.components.RecipeSearchBar
import com.example.myapplication.Recipes.navigation.Routes
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecipeHomeScreen(
    navController: NavController,
    viewModel: RecipeHomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFFE4DB40), Color(0xFF3AC42A))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        RecipeSearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onEvent(RecipeHomeEvent.SearchQueryChanged(it)) },
            onSearch = { viewModel.onEvent(RecipeHomeEvent.Search(it)) },
            suggestions = uiState.searchSuggestions,
            showSuggestions = uiState.showSuggestions,
            onSuggestionClick = { viewModel.onEvent(RecipeHomeEvent.Search(it)) },
            onClearClick = { viewModel.onEvent(RecipeHomeEvent.ClearSearch) },
            modifier = Modifier.fillMaxWidth()
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }


                    //Вот тут продукты и категории


                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    items(
                        items = uiState.recipes,
                        key = { it.id }
                    ) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onFavoriteClick = {
                                viewModel.onEvent(RecipeHomeEvent.ToggleFavorite(recipe.id))
                            },
                            onLikeClick = {
                                viewModel.onEvent(RecipeHomeEvent.ToggleLike(recipe.id))
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