package com.example.myapplication.Recipes.ui.groceryrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.use_case.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GroceryRecipeUiState(
    val exactMatchRecipes: List<Recipe> = emptyList(),
    val recipesWithMissing: List<Recipe> = emptyList(),
    val anyMatchRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val userId: Int = 1
)

sealed class GroceryRecipeEvent {
    data class LoadExactMatchRecipes(val selectedItems: List<Int>) : GroceryRecipeEvent()
    data class LoadAnyMatchRecipes(val selectedItems: List<Int>) : GroceryRecipeEvent()
    data class ToggleFavorite(val recipeId: Int) : GroceryRecipeEvent()
    data class ToggleLike(val recipeId: Int) : GroceryRecipeEvent()
}

class GroceryRecipeViewModel(
    private val getRecipesByGroceryItemsUseCase: GetRecipesByGroceryItemsUseCase,
    private val getRecipesByExactGroceryItemsUseCase: GetRecipesByExactGroceryItemsUseCase,
    private val getRecipesWithMissingItemsUseCase: GetRecipesWithMissingItemsUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val isRecipeFavoriteUseCase: IsRecipeFavoriteUseCase,
    private val addLikeUseCase: AddLikeUseCase,
    private val removeLikeUseCase: RemoveLikeUseCase,
    private val isRecipeLikedUseCase: IsRecipeLikedUseCase,
    private val getLikesCountUseCase: GetLikesCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroceryRecipeUiState())
    val uiState: StateFlow<GroceryRecipeUiState> = _uiState.asStateFlow()

    fun onEvent(event: GroceryRecipeEvent) {
        when (event) {
            is GroceryRecipeEvent.LoadExactMatchRecipes -> loadExactMatchRecipes(event.selectedItems)
            is GroceryRecipeEvent.LoadAnyMatchRecipes -> loadAnyMatchRecipes(event.selectedItems)
            is GroceryRecipeEvent.ToggleFavorite -> toggleFavorite(event.recipeId)
            is GroceryRecipeEvent.ToggleLike -> toggleLike(event.recipeId)
        }
    }

    private fun loadExactMatchRecipes(selectedItems: List<Int>) {
        if (selectedItems.isEmpty()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val userId = _uiState.value.userId

                val exactMatches = getRecipesByExactGroceryItemsUseCase(selectedItems)
                val enrichedExactMatches = exactMatches.map { recipe ->
                    recipe.copy(
                        isLiked = isRecipeLikedUseCase(userId, recipe.id),
                        isFavorite = isRecipeFavoriteUseCase(userId, recipe.id)
                    )
                }

                val withMissing = getRecipesWithMissingItemsUseCase(selectedItems)
                val enrichedWithMissing = withMissing.map { recipe ->
                    recipe.copy(
                        isLiked = isRecipeLikedUseCase(userId, recipe.id),
                        isFavorite = isRecipeFavoriteUseCase(userId, recipe.id)
                    )
                }

                _uiState.update {
                    it.copy(
                        exactMatchRecipes = enrichedExactMatches,
                        recipesWithMissing = enrichedWithMissing,
                        anyMatchRecipes = emptyList(),
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

    private fun loadAnyMatchRecipes(selectedItems: List<Int>) {
        if (selectedItems.isEmpty()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val userId = _uiState.value.userId

                val anyMatches = getRecipesByGroceryItemsUseCase(selectedItems)
                val enrichedAnyMatches = anyMatches.map { recipe ->
                    recipe.copy(
                        isLiked = isRecipeLikedUseCase(userId, recipe.id),
                        isFavorite = isRecipeFavoriteUseCase(userId, recipe.id)
                    )
                }

                _uiState.update {
                    it.copy(
                        exactMatchRecipes = emptyList(),
                        recipesWithMissing = emptyList(),
                        anyMatchRecipes = enrichedAnyMatches,
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
                state.copy(
                    exactMatchRecipes = updateRecipeFavoriteState(state.exactMatchRecipes, recipeId, !isCurrentlyFavorite),
                    recipesWithMissing = updateRecipeFavoriteState(state.recipesWithMissing, recipeId, !isCurrentlyFavorite),
                    anyMatchRecipes = updateRecipeFavoriteState(state.anyMatchRecipes, recipeId, !isCurrentlyFavorite)
                )
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
                state.copy(
                    exactMatchRecipes = updateRecipeLikeState(state.exactMatchRecipes, recipeId, !isCurrentlyLiked, newLikesCount),
                    recipesWithMissing = updateRecipeLikeState(state.recipesWithMissing, recipeId, !isCurrentlyLiked, newLikesCount),
                    anyMatchRecipes = updateRecipeLikeState(state.anyMatchRecipes, recipeId, !isCurrentlyLiked, newLikesCount)
                )
            }
        }
    }

    private fun updateRecipeFavoriteState(
        recipes: List<Recipe>,
        recipeId: Int,
        isFavorite: Boolean
    ): List<Recipe> {
        return recipes.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(isFavorite = isFavorite)
            } else {
                recipe
            }
        }
    }

    private fun updateRecipeLikeState(
        recipes: List<Recipe>,
        recipeId: Int,
        isLiked: Boolean,
        likesCount: Int
    ): List<Recipe> {
        return recipes.map { recipe ->
            if (recipe.id == recipeId) {
                recipe.copy(
                    isLiked = isLiked,
                    likesCount = likesCount
                )
            } else {
                recipe
            }
        }
    }
}