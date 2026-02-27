package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.CustomerGroupDao
import id.my.santosa.notagampang.database.dao.OrderItemDao
import id.my.santosa.notagampang.database.entity.CustomerGroupEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class CustomerGroupWithTotal(
  val group: CustomerGroupEntity,
  val totalAmount: Int,
)

class CustomerGroupRepository(
  private val customerGroupDao: CustomerGroupDao,
  private val orderItemDao: OrderItemDao,
) {
  fun getActiveGroupsWithTotals(): Flow<List<CustomerGroupWithTotal>> {
    return customerGroupDao.getGroupsWithTotalUnpaidByStatus("Active").map { groups ->
      groups.map { daoModel -> CustomerGroupWithTotal(daoModel.group, daoModel.totalUnpaid) }
    }
  }

  suspend fun createNewGroup(alias: String): Long {
    val newGroup =
      CustomerGroupEntity(
        alias = alias,
        createdAt = System.currentTimeMillis(),
        status = "Active",
      )
    return customerGroupDao.insertGroup(newGroup)
  }
}
