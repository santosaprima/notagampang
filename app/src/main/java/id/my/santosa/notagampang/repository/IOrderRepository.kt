package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

interface IOrderRepository {
  fun getOrdersForGroup(groupId: Long): Flow<List<OrderItemEntity>>

  suspend fun insertOrderItem(orderItem: OrderItemEntity): Long

  suspend fun updateOrderItems(orderItems: List<OrderItemEntity>)

  suspend fun updateOrderItem(orderItem: OrderItemEntity)

  suspend fun deleteOrder(orderItem: OrderItemEntity)

  fun getTotalPaidIncome(): Flow<Int>
}
