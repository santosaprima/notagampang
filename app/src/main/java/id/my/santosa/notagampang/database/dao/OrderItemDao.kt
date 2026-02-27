package id.my.santosa.notagampang.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import id.my.santosa.notagampang.database.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
  @Query("SELECT * FROM order_items WHERE customerGroupId = :groupId")
  fun getOrderItemsForGroup(groupId: Long): Flow<List<OrderItemEntity>>

  @Query("SELECT * FROM order_items WHERE customerGroupId = :groupId AND status = :status")
  suspend fun getOrderItemsForGroupByStatus(
    groupId: Long,
    status: String,
  ): List<OrderItemEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrderItem(orderItem: OrderItemEntity): Long

  @Update suspend fun updateOrderItem(orderItem: OrderItemEntity)

  @Delete suspend fun deleteOrderItem(orderItem: OrderItemEntity)

  @Query("DELETE FROM order_items WHERE customerGroupId = :groupId")
  suspend fun deleteOrderItemsForGroup(groupId: Long)
}
