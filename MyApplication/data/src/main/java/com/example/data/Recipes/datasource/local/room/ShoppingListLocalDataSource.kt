package com.example.data.Recipes.datasource.local.room

import com.example.data.Recipes.datasource.local.room.model.ShoppingListItemLocalEntity
import com.example.data.Recipes.datasource.local.room.model.ShoppingListLocalEntity

interface ShoppingListLocalDataSource {

    suspend fun getLists(userId: Int): List<ShoppingListLocalEntity>
    suspend fun findList(id: Int): ShoppingListLocalEntity?
    suspend fun upsertList(list: ShoppingListLocalEntity)
    suspend fun upsertLists(lists: List<ShoppingListLocalEntity>)
    suspend fun deleteList(id: Int)
    suspend fun pendingCreateLists(userId: Int): List<ShoppingListLocalEntity>
    suspend fun pendingUpdateLists(userId: Int): List<ShoppingListLocalEntity>
    suspend fun pendingDeleteLists(userId: Int): List<ShoppingListLocalEntity>
    suspend fun replaceAllSyncedLists(userId: Int, newRows: List<ShoppingListLocalEntity>)

    suspend fun getItems(listId: Int): List<ShoppingListItemLocalEntity>
    suspend fun findItem(id: Int): ShoppingListItemLocalEntity?
    suspend fun upsertItem(item: ShoppingListItemLocalEntity)
    suspend fun upsertItems(items: List<ShoppingListItemLocalEntity>)
    suspend fun deleteItem(id: Int)
    suspend fun deleteAllItemsOfList(listId: Int)
    suspend fun clearCompleted(listId: Int)
    suspend fun pendingCreateItems(userId: Int): List<ShoppingListItemLocalEntity>
    suspend fun pendingUpdateItems(userId: Int): List<ShoppingListItemLocalEntity>
    suspend fun pendingDeleteItems(userId: Int): List<ShoppingListItemLocalEntity>
}
