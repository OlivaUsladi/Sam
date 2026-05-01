package com.example.myapplication.Recipes.ui.categoryrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.use_case.AddLikeUseCase
import com.example.domain.Recipes.use_case.AddToFavoritesUseCase
import com.example.domain.Recipes.use_case.GetLikesCountUseCase
import com.example.domain.Recipes.use_case.GetRecipesByCategoryUseCase
import com.example.domain.Recipes.use_case.IsRecipeFavoriteUseCase
import com.example.domain.Recipes.use_case.IsRecipeLikedUseCase
import com.example.domain.Recipes.use_case.RemoveFromFavoritesUseCase
import com.example.domain.Recipes.use_case.RemoveLikeUseCase
import com.example.domain.Recipes.use_case.SearchRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryRecipesUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchSuggestions: List<String> = emptyList(),
    val showSuggestions: Boolean = false,
    val userId: Int = 1
)

sealed class CategoryRecipesEvent {
    data class LoadRecipes(val categoryId: Int) : CategoryRecipesEvent()
    data class SearchQueryChanged(val query: String) : CategoryRecipesEvent()
    data class Search(val query: String) : CategoryRecipesEvent()
    data class ToggleFavorite(val recipeId: Int) : CategoryRecipesEvent()
    data class ToggleLike(val recipeId: Int) : CategoryRecipesEvent()
    data object ClearSearch : CategoryRecipesEvent()
}

class CategoryRecipesViewModel(
    private val categoryId: Int,
    private val categoryName: String,
    private val getRecipesByCategoryUseCase: GetRecipesByCategoryUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val isRecipeFavoriteUseCase: IsRecipeFavoriteUseCase,
    private val addLikeUseCase: AddLikeUseCase,
    private val removeLikeUseCase: RemoveLikeUseCase,
    private val isRecipeLikedUseCase: IsRecipeLikedUseCase,
    private val getLikesCountUseCase: GetLikesCountUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryRecipesUiState())
    val uiState: StateFlow<CategoryRecipesUiState> = _uiState.asStateFlow()

    private var allRecipes: List<Recipe> = emptyList()

    init {
        loadRecipes()
    }

    fun onEvent(event: CategoryRecipesEvent) {
        when (event) {
            is CategoryRecipesEvent.LoadRecipes -> loadRecipes(event.categoryId)
            is CategoryRecipesEvent.SearchQueryChanged -> updateSuggestions(event.query)
            is CategoryRecipesEvent.Search -> search(event.query)
            is CategoryRecipesEvent.ToggleFavorite -> toggleFavorite(event.recipeId)
            is CategoryRecipesEvent.ToggleLike -> toggleLike(event.recipeId)
            CategoryRecipesEvent.ClearSearch -> clearSearch()
        }
    }

    private fun loadRecipes(categoryId: Int = this.categoryId) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val recipes = getRecipesByCategoryUseCase(categoryId)
                allRecipes = recipes

                val enrichedRecipes = recipes.map { recipe ->
                    recipe.copy(
                        isLiked = isRecipeLikedUseCase(_uiState.value.userId, recipe.id),
                        isFavorite = isRecipeFavoriteUseCase(_uiState.value.userId, recipe.id)
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
            _uiState.update { it.copy(searchQuery = query, isLoading = true, showSuggestions = false) }

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
        _uiState.update { it.copy(searchQuery = "", searchSuggestions = emptyList()) }
        loadRecipes()
    }
}
