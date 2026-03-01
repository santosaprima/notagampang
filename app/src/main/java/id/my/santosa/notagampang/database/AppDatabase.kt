package id.my.santosa.notagampang.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.my.santosa.notagampang.database.dao.CategoryDao
import id.my.santosa.notagampang.database.dao.CustomerGroupDao
import id.my.santosa.notagampang.database.dao.DebtRecordDao
import id.my.santosa.notagampang.database.dao.MenuItemDao
import id.my.santosa.notagampang.database.dao.OrderItemDao
import id.my.santosa.notagampang.database.dao.SuggestionPresetDao
import id.my.santosa.notagampang.database.entity.CategoryEntity
import id.my.santosa.notagampang.database.entity.CustomerGroupEntity
import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import id.my.santosa.notagampang.database.entity.OrderItemEntity
import id.my.santosa.notagampang.database.entity.SuggestionPresetEntity

@Database(
        entities =
                [
                        CustomerGroupEntity::class,
                        MenuItemEntity::class,
                        OrderItemEntity::class,
                        DebtRecordEntity::class,
                        SuggestionPresetEntity::class,
                        CategoryEntity::class,
                ],
        version = 3,
        exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
        abstract fun customerGroupDao(): CustomerGroupDao

        abstract fun menuItemDao(): MenuItemDao

        abstract fun orderItemDao(): OrderItemDao

        abstract fun debtRecordDao(): DebtRecordDao

        abstract fun suggestionPresetDao(): SuggestionPresetDao

        abstract fun categoryDao(): CategoryDao

        companion object {
                @Volatile private var instance: AppDatabase? = null

                fun getDatabase(context: Context): AppDatabase {
                        return instance
                                ?: synchronized(this) {
                                        val newInstance =
                                                Room.databaseBuilder(
                                                                context.applicationContext,
                                                                AppDatabase::class.java,
                                                                "notagampang_database",
                                                        )
                                                        .fallbackToDestructiveMigration()
                                                        .build()
                                        instance = newInstance
                                        newInstance
                                }
                }
        }
}
