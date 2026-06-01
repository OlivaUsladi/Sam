package com.example.data.Recipes.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.Recipes.datasource.local.room.model.ShoppingListItemLocalEntity
import com.example.data.Recipes.datasource.local.room.model.ShoppingListLocalEntity

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shop_lists WHERE userId = :userId AND pendingDelete = 0 " +
            "ORDER BY createdAt DESC, id DESC")
    suspend fun getLists(userId: Int): List<ShoppingListLocalEntity>

    @Query("SELECT * FROM shop_lists WHERE id = :id LIMIT 1")
    suspend fun findList(id: Int): ShoppingListLocalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertList(list: ShoppingListLocalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLists(lists: List<ShoppingListLocalEntity>)

    @Query("DELETE FROM shop_lists WHERE id = :id")
    suspend fun deleteList(id: Int)

    @Query("DELETE FROM shop_lists WHERE userId = :userId AND pendingCreate = 0 AND pendingDelete = 0 AND pendingUpdate = 0")
    suspend fun deleteAllSyncedLists(userId: Int)

    @Query("SELECT * FROM shop_lists WHERE pendingCreate = 1 AND userId = :userId")
    suspend fun pendingCreateLists(userId: Int): List<ShoppingListLocalEntity>

    @Query("SELECT * FROM shop_lists WHERE pendingUpdate = 1 AND userId = :userId")
    suspend fun pendingUpdateLists(userId: Int): List<ShoppingListLocalEntity>

    @Query("SELECT * FROM shop_lists WHERE pendingDelete = 1 AND userId = :userId")
    suspend fun pendingDeleteLists(userId: Int): List<ShoppingListLocalEntity>


    @Query("SELECT * FROM shop_list_items WHERE listId = :listId AND pendingDelete = 0 ORDER BY id ASC")
    suspend fun getItems(listId: Int): List<ShoppingListItemLocalEntity>

    @Query("SELECT * FROM shop_list_items WHERE id = :id LIMIT 1")
    suspend fun findItem(id: Int): ShoppingListItemLocalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItem(item: ShoppingListItemLocalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItems(items: List<ShoppingListItemLocalEntity>)

    @Query("DELETE FROM shop_list_items WHERE id = :id")
    suspend fun deleteItem(id: Int)

    @Query("DELETE FROM shop_list_items WHERE listId = :listId")
    suspend fun deleteAllItemsOfList(listId: Int)

    @Query("DELETE FROM shop_list_items WHERE isChecked = 1 AND listId = :listId")
    suspend fun clearCompleted(listId: Int)

    @Query("SELECT * FROM shop_list_items WHERE pendingCreate = 1 AND listId " +
            "IN (SELECT id FROM shop_lists WHERE userId = :userId)")
    suspend fun pendingCreateItems(userId: Int): List<ShoppingListItemLocalEntity>

    @Query("SELECT * FROM shop_list_items WHERE pendingUpdate = 1 AND listId " +
            "IN (SELECT id FROM shop_lists WHERE userId = :userId)")
    suspend fun pendingUpdateItems(userId: Int): List<ShoppingListItemLocalEntity>

    @Query("SELECT * FROM shop_list_items WHERE pendingDelete = 1 AND listId " +
            "IN (SELECT id FROM shop_lists WHERE userId = :userId)")
    suspend fun pendingDeleteItems(userId: Int): List<ShoppingListItemLocalEntity>
}
