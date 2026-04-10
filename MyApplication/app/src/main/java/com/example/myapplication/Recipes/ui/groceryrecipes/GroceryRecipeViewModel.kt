package com.example.myapplication.Recipes.ui.groceryrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Recipes.model.Grocery
import com.example.domain.Recipes.model.GroceryItem
import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.use_case.GetRecipesByExactGroceryItemsUseCase
import com.example.domain.Recipes.use_case.GetRecipesByGroceryItemsUseCase
import com.example.domain.Recipes.use_case.GetRecipesWithMissingItemsUseCase
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
    val error: String? = null
)

sealed class GroceryRecipeEvent {
    data class LoadExactMatchRecipes(val selectedItems: List<Int>) : GroceryRecipeEvent()
    data class LoadAnyMatchRecipes(val selectedItems: List<Int>) : GroceryRecipeEvent()
}

class GroceryRecipeViewModel (
    private val getRecipesByGroceryItemsUseCase: GetRecipesByGroceryItemsUseCase,
    private val getRecipesByExactGroceryItemsUseCase: GetRecipesByExactGroceryItemsUseCase,
    private val getRecipesWithMissingItemsUseCase: GetRecipesWithMissingItemsUseCase
 ) : ViewModel() {
    private val _uiState = MutableStateFlow(GroceryRecipeUiState())
    val uiState: StateFlow<GroceryRecipeUiState> = _uiState.asStateFlow()


    fun onEvent(event: GroceryRecipeEvent) {
        when (event) {
            is GroceryRecipeEvent.LoadExactMatchRecipes -> loadExactMatchRecipes(event.selectedItems)
            is GroceryRecipeEvent.LoadAnyMatchRecipes -> loadAnyMatchRecipes(event.selectedItems)
        }
    }

    private fun loadExactMatchRecipes(selectedItems: List<Int>) {
        if (selectedItems.isEmpty()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val exactMatches = getRecipesByExactGroceryItemsUseCase(selectedItems)

                val withMissing = getRecipesWithMissingItemsUseCase(selectedItems)

                _uiState.update {
                    it.copy(
                        exactMatchRecipes = exactMatches,
                        recipesWithMissing = withMissing,
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

                val anyMatches = getRecipesByGroceryItemsUseCase(selectedItems)

                _uiState.update {
                    it.copy(
                        exactMatchRecipes = emptyList(),
                        recipesWithMissing = emptyList(),
                        anyMatchRecipes = anyMatches,
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
}