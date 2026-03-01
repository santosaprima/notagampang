package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.OrderItemDao
import id.my.santosa.notagampang.database.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val orderItemDao: OrderItemDao) {
        fun getOrdersForGroup(groupId: Long): Flow<List<OrderItemEntity>> =
                orderItemDao.getOrderItemsForGroup(groupId)

        suspend fun insertOrderItem(orderItem: OrderItemEntity) =
                orderItemDao.insertOrderItem(orderItem)

        suspend fun updateOrderItems(orderItems: List<OrderItemEntity>) =
                orderItemDao.updateOrderItems(orderItems)

        suspend fun updateOrderItem(orderItem: OrderItemEntity) =
                orderItemDao.updateOrderItem(orderItem)

        suspend fun deleteOrder(orderItem: OrderItemEntity) =
                orderItemDao.deleteOrderItem(orderItem)

        fun getTotalPaidIncome(): Flow<Int> = orderItemDao.getTotalPaidIncome()
}
