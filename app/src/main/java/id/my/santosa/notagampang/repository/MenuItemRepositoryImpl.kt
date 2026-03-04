package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.MenuItemDao
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import kotlinx.coroutines.flow.Flow

class MenuItemRepositoryImpl(private val menuItemDao: MenuItemDao) : IMenuItemRepository {
  override fun getAllMenuItems(): Flow<List<MenuItemEntity>> = menuItemDao.getAllMenuItems()

  override suspend fun insertMenuItem(menuItem: MenuItemEntity) = menuItemDao.insertMenuItem(menuItem)

  override suspend fun deleteMenuItem(menuItem: MenuItemEntity) = menuItemDao.deleteMenuItem(menuItem)

  override suspend fun getCount(): Int = menuItemDao.getCount()
}
