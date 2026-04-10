package com.example.myapplication.Recipes.ui.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Recipes.model.Grocery
import com.example.domain.Recipes.model.GroceryItem
import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.use_case.GetGroceriesUseCase
import com.example.domain.Recipes.use_case.GetGroceryItemsUseCase
import com.example.domain.Recipes.use_case.GetRecipesByExactGroceryItemsUseCase
import com.example.domain.Recipes.use_case.GetRecipesByGroceryItemsUseCase
import com.example.domain.Recipes.use_case.GetRecipesWithMissingItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class GroceriesUiState(
    val groceries: List<Grocery> = emptyList(),
    val groceryItems: List<GroceryItem> = emptyList(),
    val expandedCategories: Set<Int> = emptySet(),
    val selectedItems: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val onlyGroceries: Boolean = false
)

sealed class GroceriesEvent {
    data class ToggleGroceryItem(val itemId: Int) : GroceriesEvent()
    data class ToggleGrocery(val categoryId: Int) : GroceriesEvent()
    data object ClearSelection : GroceriesEvent()
    data object Reset : GroceriesEvent()
    data object SelectOnly: GroceriesEvent()
}
class GroceriesViewModel (
    private val getGroceriesUseCase: GetGroceriesUseCase,
    private val getGroceryItemsUseCase: GetGroceryItemsUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(GroceriesUiState())
    val uiState: StateFlow<GroceriesUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: GroceriesEvent) {
        when (event) {
            is GroceriesEvent.ToggleGroceryItem -> toggleItem(event.itemId)
            is GroceriesEvent.ToggleGrocery -> toggleGrocery(event.categoryId)
            GroceriesEvent.ClearSelection -> clearSelection()
            GroceriesEvent.Reset -> reset()
            GroceriesEvent.SelectOnly -> changeSearch()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val groceries = getGroceriesUseCase()
                val groceryItems = getGroceryItemsUseCase()
                _uiState.update {
                    it.copy(
                        groceries = groceries,
                        groceryItems = groceryItems,
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

    private fun toggleItem(itemId: Int) {
        _uiState.update { state ->
            val newSelected = if (state.selectedItems.contains(itemId)) {
                state.selectedItems - itemId
            } else {
                state.selectedItems + itemId
            }
            state.copy(selectedItems = newSelected)
        }
    }


    private fun changeSearch() {
        _uiState.update { it.copy(onlyGroceries = !it.onlyGroceries) }
    }

    private fun clearSelection() {
        _uiState.update {
            it.copy(
                selectedItems = emptySet()
            )
        }
    }

    private fun toggleGrocery(categoryId: Int) {
        _uiState.update { state ->
            val newExpanded = if (state.expandedCategories.contains(categoryId)) {
                state.expandedCategories - categoryId
            } else {
                state.expandedCategories + categoryId
            }
            state.copy(expandedCategories = newExpanded)
        }
    }

    private fun reset() {
        clearSelection()
        loadData()
    }


}