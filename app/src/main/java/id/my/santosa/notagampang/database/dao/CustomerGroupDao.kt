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

  @Query(
    """
    SELECT cg.*, COALESCE(SUM(oi.priceAtOrder * oi.quantity), 0) as totalUnpaid 
    FROM customer_groups cg 
    LEFT JOIN order_items oi ON cg.id = oi.customerGroupId AND oi.status = 'Unpaid' 
    WHERE cg.status = :status 
    GROUP BY cg.id 
    ORDER BY cg.createdAt DESC
  """,
  )
  fun getGroupsWithTotalUnpaidByStatus(status: String): Flow<List<CustomerGroupWithTotalDaoModel>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGroup(group: CustomerGroupEntity): Long

  @Update suspend fun updateGroup(group: CustomerGroupEntity)

  @Delete suspend fun deleteGroup(group: CustomerGroupEntity)
}
