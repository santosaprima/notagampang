package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.OrderItemDao
import id.my.santosa.notagampang.database.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderItemDao: OrderItemDao) {
  fun getOrdersForGroup(groupId: Long): Flow<List<OrderItemEntity>> = orderItemDao.getOrderItemsForGroup(groupId)

  suspend fun addOrUpdateOrderItem(orderItem: OrderItemEntity) {
    // Basic logic: if item with same group and menuItemId exists (and is Unpaid), increment it.
    // For now we'll just insert/replace as per simple MVP.
    orderItemDao.insertOrderItem(orderItem)
  }

  suspend fun deleteOrder(orderItem: OrderItemEntity) = orderItemDao.deleteOrderItem(orderItem)
}
