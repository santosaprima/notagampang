package id.my.santosa.notagampang.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.my.santosa.notagampang.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories ORDER BY name ASC")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity)

  @Delete suspend fun deleteCategory(category: CategoryEntity)

  @Query("SELECT COUNT(*) FROM categories")
  suspend fun getCount(): Int
}
