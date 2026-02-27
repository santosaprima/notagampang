package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.MenuItemDao
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import kotlinx.coroutines.flow.Flow

class MenuItemRepository(private val menuItemDao: MenuItemDao) {
  fun getAllMenuItems(): Flow<List<MenuItemEntity>> = menuItemDao.getAllMenuItems()

  suspend fun insertMenuItem(menuItem: MenuItemEntity) = menuItemDao.insertMenuItem(menuItem)

  suspend fun getCount(): Int {
    // We can add a simple count query to DAO later if needed,
    // for now we'll just help MainActivity seed if list is empty.
    return 0
  }
}
