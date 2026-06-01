package com.example.data.Recipes.datasource.local.room

import com.example.data.Recipes.datasource.local.room.dao.ShoppingListDao
import com.example.data.Recipes.datasource.local.room.model.ShoppingListItemLocalEntity
import com.example.data.Recipes.datasource.local.room.model.ShoppingListLocalEntity

class ShoppingListLocalDataSourceImpl(
    private val dao: ShoppingListDao,
) : ShoppingListLocalDataSource {

    override suspend fun getLists(userId: Int) = dao.getLists(userId)
    override suspend fun findList(id: Int) = dao.findList(id)
    override suspend fun upsertList(list: ShoppingListLocalEntity) = dao.upsertList(list)
    override suspend fun upsertLists(lists: List<ShoppingListLocalEntity>) = dao.upsertLists(lists)
    override suspend fun deleteList(id: Int) = dao.deleteList(id)
    override suspend fun pendingCreateLists(userId: Int) = dao.pendingCreateLists(userId)
    override suspend fun pendingUpdateLists(userId: Int) = dao.pendingUpdateLists(userId)
    override suspend fun pendingDeleteLists(userId: Int) = dao.pendingDeleteLists(userId)

    override suspend fun replaceAllSyncedLists(
        userId: Int, newRows: List<ShoppingListLocalEntity>,
    ) {
        val pendingIds = (dao.pendingUpdateLists(userId) + dao.pendingDeleteLists(userId))
            .map { it.id }.toHashSet()
        dao.deleteAllSyncedLists(userId)
        val filtered = newRows.filterNot { it.id in pendingIds }
        if (filtered.isNotEmpty()) dao.upsertLists(filtered)
    }

    override suspend fun getItems(listId: Int) = dao.getItems(listId)
    override suspend fun findItem(id: Int) = dao.findItem(id)
    override suspend fun upsertItem(item: ShoppingListItemLocalEntity) = dao.upsertItem(item)
    override suspend fun upsertItems(items: List<ShoppingListItemLocalEntity>) = dao.upsertItems(items)
    override suspend fun deleteItem(id: Int) = dao.deleteItem(id)
    override suspend fun deleteAllItemsOfList(listId: Int) = dao.deleteAllItemsOfList(listId)
    override suspend fun clearCompleted(listId: Int) = dao.clearCompleted(listId)
    override suspend fun pendingCreateItems(userId: Int) = dao.pendingCreateItems(userId)
    override suspend fun pendingUpdateItems(userId: Int) = dao.pendingUpdateItems(userId)
    override suspend fun pendingDeleteItems(userId: Int) = dao.pendingDeleteItems(userId)
}
