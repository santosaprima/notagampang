package id.my.santosa.notagampang.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import id.my.santosa.notagampang.database.entity.CustomerGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerGroupDao {
  @Query("SELECT * FROM customer_groups ORDER BY createdAt DESC")
  fun getAllGroups(): Flow<List<CustomerGroupEntity>>

  @Query("SELECT * FROM customer_groups WHERE status = :status ORDER BY createdAt DESC")
  fun getGroupsByStatus(status: String): Flow<List<CustomerGroupEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGroup(group: CustomerGroupEntity): Long

  @Update suspend fun updateGroup(group: CustomerGroupEntity)

  @Delete suspend fun deleteGroup(group: CustomerGroupEntity)
}
