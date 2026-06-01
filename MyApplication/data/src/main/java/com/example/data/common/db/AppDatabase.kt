package com.example.data.common.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.Finance.datasource.local.dao.GoalDao
import com.example.data.Finance.datasource.local.dao.SourceDao
import com.example.data.Finance.datasource.local.dao.TagDao
import com.example.data.Finance.datasource.local.dao.TransactionDao
import com.example.data.Finance.datasource.local.model.GoalLocalEntity
import com.example.data.Finance.datasource.local.model.SourceLocalEntity
import com.example.data.Finance.datasource.local.model.TagLocalEntity
import com.example.data.Finance.datasource.local.model.TransactionLocalEntity
import com.example.data.Recipes.datasource.local.room.dao.ShoppingListDao
import com.example.data.Recipes.datasource.local.room.model.ShoppingListItemLocalEntity
import com.example.data.Recipes.datasource.local.room.model.ShoppingListLocalEntity

@Database(
    entities = [
        SourceLocalEntity::class,
        TagLocalEntity::class,
        GoalLocalEntity::class,
        TransactionLocalEntity::class,
        ShoppingListLocalEntity::class,
        ShoppingListItemLocalEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sourceDao(): SourceDao
    abstract fun tagDao(): TagDao
    abstract fun goalDao(): GoalDao
    abstract fun transactionDao(): TransactionDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        const val DB_NAME = "sam1.db"

        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME,
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}
