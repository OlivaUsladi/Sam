package com.example.myapplication.Recipes.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.use_case.AddLikeUseCase
import com.example.domain.Recipes.use_case.GetFavoriteRecipesUseCase
import com.example.domain.Recipes.use_case.GetLikesCountUseCase
import com.example.domain.Recipes.use_case.IsRecipeLikedUseCase
import com.example.domain.Recipes.use_case.RemoveFromFavoritesUseCase
import com.example.domain.Recipes.use_case.RemoveLikeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavouriteUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchSuggestions: List<String> = emptyList(),
    val showSuggestions: Boolean = false,
    val userId: Int = 1
)

sealed class FavouriteEvent {
    data class SearchQueryChanged(val query: String) : FavouriteEvent()
    data class Search(val query: String) : FavouriteEvent()
    data class ToggleFavorite(val recipeId: Int) : FavouriteEvent()
    data class ToggleLike(val recipeId: Int) : FavouriteEvent()
    data object ClearSearch : FavouriteEvent()
    data object LoadFavourites : FavouriteEvent()
}

class FavouriteRecipeViewModel(
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val addLikeUseCase: AddLikeUseCase,
    private val removeLikeUseCase: RemoveLikeUseCase,
    private val isRecipeLikedUseCase: IsRecipeLikedUseCase,
    private val getLikesCountUseCase: GetLikesCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouriteUiState())
    val uiState: StateFlow<FavouriteUiState> = _uiState.asStateFlow()

    private var allRecipes: List<Recipe> = emptyList()

    init {
        loadFavourites()
    }

    fun onEvent(event: FavouriteEvent) {
        when (event) {
            is FavouriteEvent.SearchQueryChanged -> updateSuggestions(event.query)
            is FavouriteEvent.Search -> search(event.query)
            is FavouriteEvent.ToggleFavorite -> toggleFavorite(event.recipeId)
            is FavouriteEvent.ToggleLike -> toggleLike(event.recipeId)
            FavouriteEvent.ClearSearch -> clearSearch()
            FavouriteEvent.LoadFavourites -> loadFavourites()
        }
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = _uiState.value.userId
                val recipes = getFavoriteRecipesUseCase(userId)

                val enrichedRecipes = recipes.map { recipe ->
                    recipe.copy(
                        isFavorite = true,
                        isLiked = isRecipeLikedUseCase(userId, recipe.id)
                    )
                }

                allRecipes = enrichedRecipes

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
                    .distinct()
                state.copy(
                    searchQuery = query,
                    searchSuggestions = suggestions,
                    showSuggestions = suggestions.isNotEmpty()
                )
            }
        }
    }

    private fun search(query: String) {
        _uiState.update { it.copy(searchQuery = query, isLoading = true, showSuggestions = false) }

        if (query.isBlank()) {
            loadFavourites()
            return
        }

        try {
            val results = allRecipes.filter { recipe ->
                recipe.title.contains(query, ignoreCase = true) ||
                        recipe.description?.contains(query, ignoreCase = true) == true
            }
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

    private fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            removeFromFavoritesUseCase(userId, recipeId)

            _uiState.update { state ->
                val updatedRecipes = state.recipes.filter { it.id != recipeId }
                state.copy(recipes = updatedRecipes)
            }

            allRecipes = allRecipes.filter { it.id != recipeId }
        }
    }

    private fun toggleLike(recipeId: Int) {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            val recipe = _uiState.value.recipes.find { it.id == recipeId }
            val isCurrentlyLiked = recipe?.isLiked ?: false

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

            allRecipes = allRecipes.map { recipe ->
                if (recipe.id == recipeId) {
                    recipe.copy(
                        isLiked = !isCurrentlyLiked,
                        likesCount = newLikesCount
                    )
                } else recipe
            }
        }
    }

    private fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", searchSuggestions = emptyList()) }
        loadFavourites()
    }
}