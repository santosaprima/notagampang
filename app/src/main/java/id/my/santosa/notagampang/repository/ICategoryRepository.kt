package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {
  fun getAllCategories(): Flow<List<CategoryEntity>>

  suspend fun insertCategory(category: CategoryEntity)

  suspend fun deleteCategory(category: CategoryEntity)

  suspend fun getCount(): Int
}
