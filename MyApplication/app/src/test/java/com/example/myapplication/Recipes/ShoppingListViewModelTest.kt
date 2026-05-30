package com.example.myapplication.Recipes.ui.shoppinglist

import com.example.domain.Recipes.model.GroceryItem
import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.model.ShoppingListItem
import com.example.domain.Recipes.use_case.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingListViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val getLists: GetShoppingListsUseCase = mock()
    private val createList: CreateShoppingListUseCase = mock()
    private val renameList: UpdateShoppingListNameUseCase = mock()
    private val deleteList: DeleteShoppingListUseCase = mock()
    private val toggleItem: ToggleShoppingListItemUseCase = mock()
    private val removeItem: RemoveShoppingListItemUseCase = mock()
    private val addItem: AddItemToListUseCase = mock()
    private val mergeLists: MergeShoppingListsUseCase = mock()
    private val getGroceries: GetGroceryItemsUseCase = mock()

    private lateinit var vm: ShoppingListViewModel

    private fun shoppingList(
        id: Int,
        name: String,
        items: MutableList<ShoppingListItem> = mutableListOf()
    ) = ShoppingList(
        id = id,
        userId = 1,
        name = name,
        createdAt = LocalDateTime.of(2026, 1, 1, 0, 0),
        items = items
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        runBlocking {
            whenever(getGroceries.invoke()).thenReturn(listOf())
        }
        vm = ShoppingListViewModel(
            getLists, createList, renameList, deleteList,
            toggleItem, removeItem, addItem, mergeLists, getGroceries
        )
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `init loads grocery suggestions`() = runTest(dispatcher) {
        whenever(getGroceries.invoke()).thenReturn(
            listOf(
                GroceryItem(1, 1, "Молоко", "г"),
                GroceryItem(2, 3, "Хлеб", "шт")
            )
        )
        val vm2 = ShoppingListViewModel(
            getLists, createList, renameList, deleteList,
            toggleItem, removeItem, addItem, mergeLists, getGroceries
        )
        advanceUntilIdle()
        assertEquals(listOf("Молоко", "Хлеб"), vm2.uiState.value.grocerySuggestions)
    }

    @Test
    fun `loadShoppingLists populates state`() = runTest(dispatcher) {
        val lists = listOf(
            shoppingList(1, "Завтрак"),
            shoppingList(2, "Ужин")
        )
        whenever(getLists.invoke(1)).thenReturn(lists)

        vm.onEvent(ShoppingListEvent.LoadShoppingLists)
        advanceUntilIdle()

        assertEquals(lists, vm.uiState.value.shoppingLists)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `createList calls useCase and reloads`() = runTest(dispatcher) {
        whenever(createList.invoke(1, "Новый")).thenReturn(mock())
        whenever(getLists.invoke(1)).thenReturn(listOf())

        vm.onEvent(ShoppingListEvent.CreateList("Новый"))
        advanceUntilIdle()

        verify(createList).invoke(1, "Новый")
    }

    @Test
    fun `renameList calls useCase`() = runTest(dispatcher) {
        whenever(getLists.invoke(1)).thenReturn(listOf())

        vm.onEvent(ShoppingListEvent.RenameList(5, "Новое"))
        advanceUntilIdle()

        verify(renameList).invoke(5, "Новое")
    }

    @Test
    fun `deleteList calls useCase`() = runTest(dispatcher) {
        whenever(getLists.invoke(1)).thenReturn(listOf())

        vm.onEvent(ShoppingListEvent.DeleteList(5))
        advanceUntilIdle()

        verify(deleteList).invoke(5)
    }

    @Test
    fun `toggleItem calls useCase`() = runTest(dispatcher) {
        whenever(getLists.invoke(1)).thenReturn(listOf())

        vm.onEvent(ShoppingListEvent.ToggleItem(10, true))
        advanceUntilIdle()

        verify(toggleItem).invoke(10, true)
    }

    @Test
    fun `deleteItem calls useCase`() = runTest(dispatcher) {
        whenever(getLists.invoke(1)).thenReturn(listOf())

        vm.onEvent(ShoppingListEvent.DeleteItem(10))
        advanceUntilIdle()

        verify(removeItem).invoke(10)
    }

    @Test
    fun `addItem with valid quantity calls useCase`() = runTest(dispatcher) {
        whenever(addItem.invoke(any(), any())).thenReturn(mock<ShoppingListItem>())
        whenever(getLists.invoke(1)).thenReturn(listOf())

        vm.onEvent(ShoppingListEvent.AddItem(1, "Молоко", "2.5", "л"))
        advanceUntilIdle()

        val captor = argumentCaptor<ShoppingListItem>()
        verify(addItem).invoke(eq(1), captor.capture())

        val captured = captor.firstValue
        assertEquals("Молоко", captured.description)
        assertEquals(2.5, captured.quantity)
        assertEquals("л", captured.unit)
        assertFalse(captured.isChecked)
    }

    @Test
    fun `mergeLists calls useCase`() = runTest(dispatcher) {
        whenever(getLists.invoke(1)).thenReturn(listOf())

        vm.onEvent(ShoppingListEvent.MergeLists(listOf(2, 3)))
        advanceUntilIdle()

        verify(mergeLists).invoke(eq(listOf(2, 3).first()), any())
    }

    @Test
    fun `enterMergeMode sets flag`() {
        vm.onEvent(ShoppingListEvent.EnterMergeMode)
        assertTrue(vm.uiState.value.isMergeMode)
    }

    @Test
    fun `exitMergeMode clears selection`() {
        vm.onEvent(ShoppingListEvent.EnterMergeMode)
        vm.onEvent(ShoppingListEvent.ToggleMergeSelection(5))
        vm.onEvent(ShoppingListEvent.ExitMergeMode)

        assertFalse(vm.uiState.value.isMergeMode)
        assertTrue(vm.uiState.value.selectedForMerge.isEmpty())
    }

    @Test
    fun `toggleMergeSelection adds and removes ids`() {
        vm.onEvent(ShoppingListEvent.ToggleMergeSelection(5))
        assertTrue(vm.uiState.value.selectedForMerge.contains(5))

        vm.onEvent(ShoppingListEvent.ToggleMergeSelection(5))
        assertFalse(vm.uiState.value.selectedForMerge.contains(5))
    }

    @Test
    fun `toggleListExpansion toggles expandedListId`() {
        vm.onEvent(ShoppingListEvent.ToggleListExpansion(5))
        assertEquals(5, vm.uiState.value.expandedListId)

        vm.onEvent(ShoppingListEvent.ToggleListExpansion(5))
        assertNull(vm.uiState.value.expandedListId)
    }
}