package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.CategoryDao
import id.my.santosa.notagampang.database.dao.MenuItemDao
import id.my.santosa.notagampang.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
  private val categoryDao: CategoryDao,
  private val menuItemDao: MenuItemDao,
) {
  fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

  suspend fun insertCategory(category: CategoryEntity) {
    categoryDao.insertCategory(category)
  }

  suspend fun deleteCategory(category: CategoryEntity) {
    // Cascade delete: Remove all menu items in this category first
    menuItemDao.deleteMenuItemsByCategory(category.name)
    // Then delete the category itself
    categoryDao.deleteCategory(category)
  }

  suspend fun getCount(): Int = categoryDao.getCount()
}
