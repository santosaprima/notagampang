package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.entity.MenuItemEntity
import kotlinx.coroutines.flow.Flow

interface IMenuItemRepository {
  fun getAllMenuItems(): Flow<List<MenuItemEntity>>

  suspend fun insertMenuItem(menuItem: MenuItemEntity): Long

  suspend fun deleteMenuItem(menuItem: MenuItemEntity)

  suspend fun getCount(): Int
}
