package id.my.santosa.notagampang.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {
  @Query("SELECT * FROM menu_items ORDER BY name ASC")
  fun getAllMenuItems(): Flow<List<MenuItemEntity>>

  @Query("SELECT * FROM menu_items WHERE category = :category ORDER BY name ASC")
  fun getMenuItemsByCategory(category: String): Flow<List<MenuItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMenuItem(menuItem: MenuItemEntity): Long

  @Update suspend fun updateMenuItem(menuItem: MenuItemEntity)

  @Delete suspend fun deleteMenuItem(menuItem: MenuItemEntity)
}
