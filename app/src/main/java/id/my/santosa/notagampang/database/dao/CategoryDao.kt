package id.my.santosa.notagampang.database.dao

import androidx.room.*
import id.my.santosa.notagampang.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Delete suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT COUNT(*) FROM categories") suspend fun getCount(): Int
}
