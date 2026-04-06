package com.example.myapplication.Recipes.ui.home

import android.util.Log
import com.example.domain.Recipes.model.Recipe
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Recipes.use_case.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeHomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchSuggestions: List<String> = emptyList(),
    val showSuggestions: Boolean = false,
    val userId: Int = 1
)

sealed class RecipeHomeEvent {
    data class SearchQueryChanged(val query: String) : RecipeHomeEvent()
    data class Search(val query: String) : RecipeHomeEvent()
    data class ToggleFavorite(val recipeId: Int) : RecipeHomeEvent()
    data class ToggleLike(val recipeId: Int) : RecipeHomeEvent()
    data object ClearSearch : RecipeHomeEvent()
    data object LoadRecipes : RecipeHomeEvent()
}

class RecipeHomeViewModel(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val isRecipeFavoriteUseCase: IsRecipeFavoriteUseCase,
    private val addLikeUseCase: AddLikeUseCase,
    private val removeLikeUseCase: RemoveLikeUseCase,
    private val getLikesCountUseCase: GetLikesCountUseCase,
    private val isRecipeLikedUseCase: IsRecipeLikedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeHomeUiState())
    val uiState: StateFlow<RecipeHomeUiState> = _uiState.asStateFlow()

    private var allRecipes: List<Recipe> = emptyList()

    init {
        loadRecipes()
    }

    fun onEvent(event: RecipeHomeEvent) {
        when (event) {
            is RecipeHomeEvent.SearchQueryChanged -> updateSuggestions(event.query)
            is RecipeHomeEvent.Search -> search(event.query)
            is RecipeHomeEvent.ToggleFavorite -> toggleFavorite(event.recipeId)
            is RecipeHomeEvent.ToggleLike -> toggleLike(event.recipeId)
            RecipeHomeEvent.ClearSearch -> clearSearch()
            RecipeHomeEvent.LoadRecipes -> loadRecipes()
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val recipes = getRecipesUseCase()
                allRecipes = recipes

                val enrichedRecipes = recipes.map { recipe ->
                    recipe.copy(
                        isLiked = isRecipeLiked(recipe.id),
                        isFavorite = isRecipeFavorite(recipe.id)
                    )
                }

                _uiState.update {
                    it.copy(
                        recipes = enrichedRecipes,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun updateSuggestions(query: String) {
        _uiState.update { state ->
            if (query.isBlank()) {
                state.copy(
                    searchQuery = query,
                    searchSuggestions = emptyList(),
                    showSuggestions = false
                )
            } else {
                val suggestions = allRecipes
                    .filter { it.title.contains(query, ignoreCase = true) }
                    .map { it.title }

                state.copy(
                    searchQuery = query,
                    searchSuggestions = suggestions,
                    showSuggestions = suggestions.isNotEmpty()
                )
            }
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    searchQuery = query,
                    isLoading = true,
                    showSuggestions = false
                )
            }

            if (query.isBlank()) {
                loadRecipes()
                return@launch
            }

            try {
                val results = searchRecipesUseCase(query)
                _uiState.update {
                    it.copy(
                        recipes = results,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private suspend fun isRecipeFavorite(recipeId: Int): Boolean =
        isRecipeFavoriteUseCase(_uiState.value.userId, recipeId)

    private suspend fun isRecipeLiked(recipeId: Int): Boolean =
        isRecipeLikedUseCase(_uiState.value.userId, recipeId)

    private fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            val isCurrentlyFavorite = isRecipeFavoriteUseCase(userId, recipeId)

            if (isCurrentlyFavorite) {
                removeFromFavoritesUseCase(userId, recipeId)
            } else {
                addToFavoritesUseCase(userId, recipeId)
            }

            _uiState.update { state ->
                val updatedRecipes = state.recipes.map { recipe ->
                    if (recipe.id == recipeId) {
                        recipe.copy(isFavorite = !isCurrentlyFavorite)
                    } else recipe
                }
                state.copy(recipes = updatedRecipes)
            }
        }
    }

    private fun toggleLike(recipeId: Int) {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            val isCurrentlyLiked = isRecipeLikedUseCase(userId, recipeId)

            if (isCurrentlyLiked) {
                removeLikeUseCase(userId, recipeId)
            } else {
                addLikeUseCase(userId, recipeId)
            }

            val newLikesCount = getLikesCountUseCase(recipeId)

            _uiState.update { state ->
                val updatedRecipes = state.recipes.map { recipe ->
                    if (recipe.id == recipeId) {
                        recipe.copy(
                            isLiked = !isCurrentlyLiked,
                            likesCount = newLikesCount
                        )
                    } else recipe
                }
                state.copy(recipes = updatedRecipes)
            }
        }
    }

    private fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                searchSuggestions = emptyList(),
                showSuggestions = false
            )
        }
        loadRecipes()
    }
}