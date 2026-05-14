package com.example.myapplication.Recipes.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Recipes.model.ContentBlock
import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.model.RecipeContent
import com.example.domain.Recipes.model.RecipeIngredient
import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.use_case.AddItemsFromRecipeUseCase
import com.example.domain.Recipes.use_case.AddLikeUseCase
import com.example.domain.Recipes.use_case.AddToFavoritesUseCase
import com.example.domain.Recipes.use_case.CreateShoppingListUseCase
import com.example.domain.Recipes.use_case.GetLikesCountUseCase
import com.example.domain.Recipes.use_case.GetRecipeByIdUseCase
import com.example.domain.Recipes.use_case.GetRecipeContentUseCase
import com.example.domain.Recipes.use_case.GetShoppingListsUseCase
import com.example.domain.Recipes.use_case.IsRecipeFavoriteUseCase
import com.example.domain.Recipes.use_case.IsRecipeLikedUseCase
import com.example.domain.Recipes.use_case.RemoveFromFavoritesUseCase
import com.example.domain.Recipes.use_case.RemoveLikeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val recipeContent: RecipeContent? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userId: Int = 1,
    val isFavorite: Boolean = false,
    val isLiked: Boolean = false,
    val likesCount: Int = 0,

    val showShoppingListDialog: Boolean = false,
    val shoppingLists: List<ShoppingList> = emptyList(),
    val isCreatingNewList: Boolean = false,
    val newListName: String = ""
) {
    val ingredients: List<RecipeIngredient>
        get() = recipeContent?.ingredients ?: recipe?.ingredients ?: emptyList()

    val cookingSteps: List<ContentBlock>
        get() = recipeContent?.cookingSteps ?: emptyList()

}

sealed class RecipeDetailEvent {
    data class LoadRecipe(val recipeId: Int) : RecipeDetailEvent()
    data class LoadRecipeContent(val recipeId: Int) : RecipeDetailEvent()
    data object ToggleFavorite : RecipeDetailEvent()
    data object ToggleLike : RecipeDetailEvent()
    data object AddToShoppingList : RecipeDetailEvent()

    data object ShowShoppingListDialog : RecipeDetailEvent()
    data object HideShoppingListDialog : RecipeDetailEvent()
    data class SelectShoppingList(val listId: Int) : RecipeDetailEvent()
    data class UpdateNewListName(val name: String) : RecipeDetailEvent()
    data object CreateNewList : RecipeDetailEvent()
}

class RecipeScreenViewModel(
    private val recipeId: Int,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val getRecipeContentUseCase: GetRecipeContentUseCase,
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val isRecipeFavoriteUseCase: IsRecipeFavoriteUseCase,
    private val addLikeUseCase: AddLikeUseCase,
    private val removeLikeUseCase: RemoveLikeUseCase,
    private val isRecipeLikedUseCase: IsRecipeLikedUseCase,
    private val getLikesCountUseCase: GetLikesCountUseCase,

    private val getShoppingListsUseCase: GetShoppingListsUseCase,
    private val createShoppingListUseCase: CreateShoppingListUseCase,
    private val addItemsFromRecipeUseCase: AddItemsFromRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun onEvent(event: RecipeDetailEvent) {
        when (event) {
            is RecipeDetailEvent.LoadRecipe -> loadRecipe(event.recipeId)
            is RecipeDetailEvent.LoadRecipeContent -> loadRecipeContent(event.recipeId)
            RecipeDetailEvent.ToggleFavorite -> toggleFavorite()
            RecipeDetailEvent.ToggleLike -> toggleLike()
            RecipeDetailEvent.AddToShoppingList -> addToShoppingList()

            RecipeDetailEvent.ShowShoppingListDialog -> showShoppingListDialog()
            RecipeDetailEvent.HideShoppingListDialog -> hideShoppingListDialog()
            is RecipeDetailEvent.SelectShoppingList -> selectShoppingList(event.listId)
            is RecipeDetailEvent.UpdateNewListName -> updateNewListName(event.name)
            RecipeDetailEvent.CreateNewList -> createNewList()
        }
    }

    private fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = _uiState.value.userId
                val recipe = getRecipeByIdUseCase(recipeId)

                if (recipe != null) {
                    val isFavorite = isRecipeFavoriteUseCase(userId, recipeId)
                    val isLiked = isRecipeLikedUseCase(userId, recipeId)
                    val likesCount = getLikesCountUseCase(recipeId)

                    _uiState.update {
                        it.copy(
                            recipe = recipe,
                            isFavorite = isFavorite,
                            isLiked = isLiked,
                            likesCount = likesCount,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Рецепт не найден"
                        )
                    }
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

    private fun loadRecipeContent(recipeId: Int) {
        viewModelScope.launch {
            try {
                val content = getRecipeContentUseCase(recipeId)

                if (content != null) {
                    _uiState.update {
                        it.copy(
                            recipeContent = content
                        )
                    }
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

    private fun toggleFavorite() {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            val recipe = _uiState.value.recipe ?: return@launch
            val isCurrentlyFavorite = _uiState.value.isFavorite

            if (isCurrentlyFavorite) {
                removeFromFavoritesUseCase(userId, recipe.id)
            } else {
                addToFavoritesUseCase(userId, recipe.id)
            }

            _uiState.update {
                it.copy(isFavorite = !isCurrentlyFavorite)
            }
        }
    }

    private fun toggleLike() {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            val recipe = _uiState.value.recipe ?: return@launch
            val isCurrentlyLiked = _uiState.value.isLiked

            if (isCurrentlyLiked) {
                removeLikeUseCase(userId, recipe.id)
                val newLikesCount = getLikesCountUseCase(recipe.id)
                _uiState.update {
                    it.copy(
                        isLiked = false,
                        likesCount = newLikesCount
                    )
                }
            } else {
                addLikeUseCase(userId, recipe.id)
                val newLikesCount = getLikesCountUseCase(recipe.id)
                _uiState.update {
                    it.copy(
                        isLiked = true,
                        likesCount = newLikesCount
                    )
                }
            }
        }
    }

    private fun showShoppingListDialog() {
        viewModelScope.launch {
            try {
                val lists = getShoppingListsUseCase(_uiState.value.userId)
                val recipe = getRecipeByIdUseCase(recipeId)
                _uiState.update {
                    it.copy(
                        showShoppingListDialog = true,
                        shoppingLists = lists,
                        isCreatingNewList = false,
                        newListName = recipe?.title ?: ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Не удалось загрузить списки: ${e.message}")
                }
            }
        }
    }

    private fun hideShoppingListDialog() {
        _uiState.update {
            it.copy(
                showShoppingListDialog = false,
                isCreatingNewList = false,
                newListName = ""
            )
        }
    }

    private fun selectShoppingList(listId: Int) {
        viewModelScope.launch {
            val recipe = _uiState.value.recipe ?: return@launch
            val ingredients = _uiState.value.ingredients

            if (ingredients.isEmpty()) {
                _uiState.update {
                    it.copy(
                        error = "Нет ингредиентов для добавления",
                        showShoppingListDialog = false
                    )
                }
                return@launch
            }

            try {
                addItemsFromRecipeUseCase(listId, recipe.id, ingredients)
                _uiState.update {
                    it.copy(
                        showShoppingListDialog = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Ошибка: ${e.message}",
                        showShoppingListDialog = false
                    )
                }
            }
        }
    }

    private fun updateNewListName(name: String) {
        _uiState.update { it.copy(newListName = name) }
    }

    private fun createNewList() {
        viewModelScope.launch {
            val name = _uiState.value.newListName

            try {
                val newList = createShoppingListUseCase(_uiState.value.userId, name)
                selectShoppingList(newList.id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Не удалось создать список: ${e.message}")
                }
            }
        }
    }

    private fun addToShoppingList() {
        showShoppingListDialog()
    }
}