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
    val selectedItems: Set<Int> = emptySet(),
    val exactMatchRecipes: List<Recipe> = emptyList(),
    val recipesWithMissing: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val onlyGroceries: Boolean = false
)

sealed class GroceryRecipeEvent {
    data class ToggleRecipe(val itemId: Int) : GroceryRecipeEvent()
    data object SearchRecipes : GroceryRecipeEvent()
}

class GroceryRecipeViewModel (
    private val getRecipesByGroceryItemsUseCase: GetRecipesByGroceryItemsUseCase,
    private val getRecipesByExactGroceryItemsUseCase: GetRecipesByExactGroceryItemsUseCase,
    private val getRecipesWithMissingItemsUseCase: GetRecipesWithMissingItemsUseCase
 ) : ViewModel() {
    private val _uiState = MutableStateFlow(GroceryRecipeUiState())
    val uiState: StateFlow<GroceryRecipeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData(){
        viewModelScope.launch {
            val selectedIds = _uiState.value.selectedItems.toList()
            if (selectedIds.isEmpty()) return@launch

            _uiState.update { it.copy(isLoading = true) }

            try {
                val exactMatches = getRecipesByExactGroceryItemsUseCase(selectedIds)
                val recipesWithMissing = getRecipesWithMissingItemsUseCase(selectedIds)

                _uiState.update {
                    it.copy(
                        exactMatchRecipes = exactMatches,
                        recipesWithMissing = recipesWithMissing,
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