package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.OrderItemDao
import id.my.santosa.notagampang.database.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

class OrderRepositoryImpl(private val orderItemDao: OrderItemDao) : IOrderRepository {
  override fun getOrdersForGroup(groupId: Long): Flow<List<OrderItemEntity>> = orderItemDao.getOrderItemsForGroup(groupId)

  override suspend fun insertOrderItem(orderItem: OrderItemEntity): Long = orderItemDao.insertOrderItem(orderItem)

  override suspend fun updateOrderItems(orderItems: List<OrderItemEntity>) = orderItemDao.updateOrderItems(orderItems)

  override suspend fun updateOrderItem(orderItem: OrderItemEntity) = orderItemDao.updateOrderItem(orderItem)

  override suspend fun deleteOrder(orderItem: OrderItemEntity) = orderItemDao.deleteOrderItem(orderItem)

  override fun getTotalPaidIncome(): Flow<Int> = orderItemDao.getTotalPaidIncome()
}
