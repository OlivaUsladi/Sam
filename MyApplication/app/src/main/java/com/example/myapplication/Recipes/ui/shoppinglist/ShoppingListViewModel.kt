package com.example.myapplication.Recipes.ui.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.model.ShoppingListItem
import com.example.domain.Recipes.use_case.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShoppingListUiState(
    val shoppingLists: List<ShoppingList> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val expandedListId: Int? = null,
    val userId: Int = 1,
    val grocerySuggestions: List<String> = emptyList(),
    val isMergeMode: Boolean = false,
    val selectedForMerge: Set<Int> = emptySet()
)

sealed class ShoppingListEvent {
    data object LoadShoppingLists : ShoppingListEvent()
    data class CreateList(val name: String) : ShoppingListEvent()
    data class RenameList(val listId: Int, val newName: String) : ShoppingListEvent()
    data class DeleteList(val listId: Int) : ShoppingListEvent()
    data class ToggleItem(val itemId: Int, val isChecked: Boolean) : ShoppingListEvent()
    data class DeleteItem(val itemId: Int) : ShoppingListEvent()
    data class AddItem(val listId: Int, val description: String) : ShoppingListEvent()
    data class CheckAllItems(val listId: Int) : ShoppingListEvent()
    data class UncheckAllItems(val listId: Int) : ShoppingListEvent()
    data class MergeLists(val listIds: List<Int>) : ShoppingListEvent()
    data class ToggleListExpansion(val listId: Int) : ShoppingListEvent()
    data object EnterMergeMode : ShoppingListEvent()
    data object ExitMergeMode : ShoppingListEvent()
    data class ToggleMergeSelection(val listId: Int) : ShoppingListEvent()
}

class ShoppingListViewModel(
    private val getShoppingListsUseCase: GetShoppingListsUseCase,
    private val createShoppingListUseCase: CreateShoppingListUseCase,
    private val updateShoppingListNameUseCase: UpdateShoppingListNameUseCase,
    private val deleteShoppingListUseCase: DeleteShoppingListUseCase,
    private val toggleShoppingListItemUseCase: ToggleShoppingListItemUseCase,
    private val removeShoppingListItemUseCase: RemoveShoppingListItemUseCase,
    private val addItemToListUseCase: AddItemToListUseCase,
    private val mergeShoppingListsUseCase: MergeShoppingListsUseCase,
    private val getGroceryItemsUseCase: GetGroceryItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingListUiState())
    val uiState: StateFlow<ShoppingListUiState> = _uiState.asStateFlow()

    private var nextItemId = 100

    init {
        loadGrocerySuggestions()
    }

    fun onEvent(event: ShoppingListEvent) {
        when (event) {
            ShoppingListEvent.LoadShoppingLists -> loadShoppingLists()
            is ShoppingListEvent.CreateList -> createList(event.name)
            is ShoppingListEvent.RenameList -> renameList(event.listId, event.newName)
            is ShoppingListEvent.DeleteList -> deleteList(event.listId)
            is ShoppingListEvent.ToggleItem -> toggleItem(event.itemId, event.isChecked)
            is ShoppingListEvent.DeleteItem -> deleteItem(event.itemId)
            is ShoppingListEvent.AddItem -> addItem(event.listId, event.description)
            is ShoppingListEvent.CheckAllItems -> checkAllItems(event.listId)
            is ShoppingListEvent.UncheckAllItems -> uncheckAllItems(event.listId)
            is ShoppingListEvent.MergeLists -> mergeLists(event.listIds)
            is ShoppingListEvent.ToggleListExpansion -> toggleListExpansion(event.listId)
            ShoppingListEvent.EnterMergeMode -> enterMergeMode()
            ShoppingListEvent.ExitMergeMode -> exitMergeMode()
            is ShoppingListEvent.ToggleMergeSelection -> toggleMergeSelection(event.listId)
        }
    }

    private fun loadGrocerySuggestions() {
        viewModelScope.launch {
            try {
                val groceryItems = getGroceryItemsUseCase()
                _uiState.update {
                    it.copy(grocerySuggestions = groceryItems.map { item -> item.name })
                }
            } catch (e: Exception) {
                // Пупупу пока не придумала
            }
        }
    }

    private fun loadShoppingLists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val lists = getShoppingListsUseCase(_uiState.value.userId)
                _uiState.update {
                    it.copy(
                        shoppingLists = lists,
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

    private fun createList(name: String) {
        viewModelScope.launch {
            try {
                val newList = createShoppingListUseCase(_uiState.value.userId, name)
                _uiState.update { state ->
                    state.copy(
                        shoppingLists = state.shoppingLists + newList
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    private fun renameList(listId: Int, newName: String) {
        viewModelScope.launch {
            try {
                val updatedList = updateShoppingListNameUseCase(listId, newName)
                if (updatedList != null) {
                    _uiState.update { state ->
                        state.copy(
                            shoppingLists = state.shoppingLists.map { list ->
                                if (list.id == listId) list.copy(name = newName) else list
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    private fun deleteList(listId: Int) {
        viewModelScope.launch {
            try {
                deleteShoppingListUseCase(listId)
                _uiState.update { state ->
                    state.copy(
                        shoppingLists = state.shoppingLists.filter { it.id != listId },
                        expandedListId = if (state.expandedListId == listId) null else state.expandedListId,
                        selectedForMerge = state.selectedForMerge - listId
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    private fun toggleItem(itemId: Int, isChecked: Boolean) {
        viewModelScope.launch {
            try {
                toggleShoppingListItemUseCase(itemId, isChecked)
                _uiState.update { state ->
                    val updatedLists = state.shoppingLists.map { list ->
                        val updatedItems = list.items.map { item ->
                            if (item.id == itemId) item.copy(isChecked = isChecked) else item
                        }
                        list.copy(items = updatedItems.toMutableList())
                    }
                    state.copy(shoppingLists = updatedLists)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    private fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            try {
                removeShoppingListItemUseCase(itemId)
                _uiState.update { state ->
                    val updatedLists = state.shoppingLists.map { list ->
                        val updatedItems = list.items.filter { it.id != itemId }
                        list.copy(items = updatedItems.toMutableList())
                    }
                    state.copy(shoppingLists = updatedLists)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    private fun addItem(listId: Int, description: String) {
        viewModelScope.launch {
            try {
                val newItem = ShoppingListItem(
                    id = nextItemId++,
                    description = description,
                    isChecked = false
                )
                addItemToListUseCase(listId, newItem)

                _uiState.update { state ->
                    val updatedLists = state.shoppingLists.map { list ->
                        if (list.id == listId) {
                            val newItems = list.items.toMutableList()
                            newItems.add(newItem)
                            list.copy(items = newItems)
                        } else list
                    }
                    state.copy(shoppingLists = updatedLists)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    private fun checkAllItems(listId: Int) {
        _uiState.update { state ->
            val updatedLists = state.shoppingLists.map { list ->
                if (list.id == listId) {
                    val updatedItems = list.items.map { it.copy(isChecked = true) }
                    list.copy(items = updatedItems.toMutableList())
                } else list
            }
            state.copy(shoppingLists = updatedLists)
        }

        viewModelScope.launch {
            val list = _uiState.value.shoppingLists.find { it.id == listId }
            list?.items?.forEach { item ->
                if (!item.isChecked) {
                    toggleShoppingListItemUseCase(item.id, true)
                }
            }
        }
    }

    private fun uncheckAllItems(listId: Int) {
        _uiState.update { state ->
            val updatedLists = state.shoppingLists.map { list ->
                if (list.id == listId) {
                    val updatedItems = list.items.map { it.copy(isChecked = false) }
                    list.copy(items = updatedItems.toMutableList())
                } else list
            }
            state.copy(shoppingLists = updatedLists)
        }

        viewModelScope.launch {
            val list = _uiState.value.shoppingLists.find { it.id == listId }
            list?.items?.forEach { item ->
                if (item.isChecked) {
                    toggleShoppingListItemUseCase(item.id, false)
                }
            }
        }
    }

    private fun mergeLists(listIds: List<Int>) {
        viewModelScope.launch {
            try {
                if (listIds.size >= 2) {
                    val targetListId = listIds.first()
                    val sourceListIds = listIds.drop(1)
                    mergeShoppingListsUseCase(targetListId, sourceListIds)
                    loadShoppingLists()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    private fun toggleListExpansion(listId: Int) {
        _uiState.update { state ->
            state.copy(
                expandedListId = if (state.expandedListId == listId) null else listId
            )
        }
    }

    private fun enterMergeMode() {
        _uiState.update {
            it.copy(
                isMergeMode = true,
                selectedForMerge = emptySet(),
                expandedListId = null
            )
        }
    }

    private fun exitMergeMode() {
        _uiState.update {
            it.copy(
                isMergeMode = false,
                selectedForMerge = emptySet()
            )
        }
    }

    private fun toggleMergeSelection(listId: Int) {
        _uiState.update { state ->
            val newSelection = if (state.selectedForMerge.contains(listId)) {
                state.selectedForMerge - listId
            } else {
                state.selectedForMerge + listId
            }
            state.copy(selectedForMerge = newSelection)
        }
    }
}