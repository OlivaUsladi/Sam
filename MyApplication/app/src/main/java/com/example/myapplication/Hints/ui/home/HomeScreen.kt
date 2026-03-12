package com.example.myapplication.Hints.ui.home

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
import com.example.myapplication.Hints.navigation.Routes
import com.example.myapplication.Hints.components.ArticleCard
import com.example.myapplication.Hints.components.CategoriesBanner
import com.example.myapplication.Hints.components.HintsSearchBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFF2670CC), Color(0xFF26CCAD))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        HintsSearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onEvent(HomeEvent.SearchQueryChanged(it)) },
            onSearch = { viewModel.onEvent(HomeEvent.Search(it)) },
            suggestions = uiState.searchSuggestions,
            showSuggestions = uiState.showSuggestions,
            onSuggestionClick = { viewModel.onEvent(HomeEvent.Search(it)) },
            onClearClick = { viewModel.onEvent(HomeEvent.ClearSearch) },
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

                    item {
                        CategoriesBanner(
                            onClick = {
                                navController.navigate(Routes.Category.route)
                            },
                            brush = brush,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    items(
                        items = uiState.articles,
                        key = { it.id }
                    ) { article ->
                        ArticleCard(
                            article = article,
                            onFavoriteClick = {
                                viewModel.onEvent(HomeEvent.ToggleFavorite(article.id))
                            },
                            onLikeClick = {
                                viewModel.onEvent(HomeEvent.ToggleLike(article.id))
                            },
                            onArticleClick = {
                                navController.navigate("${Routes.Article.route}/${article.id}")
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