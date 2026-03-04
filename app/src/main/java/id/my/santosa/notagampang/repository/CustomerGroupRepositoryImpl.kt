package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.CustomerGroupDao
import id.my.santosa.notagampang.database.dao.OrderItemDao
import id.my.santosa.notagampang.database.entity.CustomerGroupEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class CustomerGroupWithTotal(
  val group: CustomerGroupEntity,
  val totalAmount: Int,
  val itemCount: Int,
)

class CustomerGroupRepositoryImpl(
  private val customerGroupDao: CustomerGroupDao,
  private val orderItemDao: OrderItemDao,
) : ICustomerGroupRepository {
  override fun getActiveGroupsWithTotals(): Flow<List<CustomerGroupWithTotal>> {
    return customerGroupDao.getGroupsWithTotalUnpaidByStatus("Active").map { groups ->
      groups.map { daoModel ->
        CustomerGroupWithTotal(daoModel.group, daoModel.totalUnpaid, daoModel.itemCount)
      }
    }
  }

  override fun getInactiveGroupsWithTotals(): Flow<List<CustomerGroupWithTotal>> {
    return customerGroupDao.getGroupsWithTotalByStatuses(listOf("Paid", "Kasbon")).map { groups ->
      groups.map { daoModel ->
        CustomerGroupWithTotal(daoModel.group, daoModel.totalUnpaid, daoModel.itemCount)
      }
    }
  }

  override suspend fun getGroupById(groupId: Long): CustomerGroupEntity? = customerGroupDao.getGroupById(groupId)

  override fun getGroupFlowById(groupId: Long): Flow<CustomerGroupEntity?> = customerGroupDao.getGroupFlowById(groupId)

  override suspend fun createNewGroup(alias: String): Long {
    val newGroup =
      CustomerGroupEntity(
        alias = alias,
        createdAt = System.currentTimeMillis(),
        status = "Active",
      )
    return customerGroupDao.insertGroup(newGroup)
  }

  override suspend fun updateGroup(group: CustomerGroupEntity) {
    customerGroupDao.updateGroup(group)
  }

  override suspend fun deleteGroup(groupId: Long) {
    val group = customerGroupDao.getGroupById(groupId)
    if (group != null) {
      customerGroupDao.deleteGroup(group)
    }
  }

  override suspend fun deletePaidGroups() = customerGroupDao.deletePaidGroups()

  override suspend fun mergeGroups(
    sourceId: Long,
    targetId: Long,
  ) {
    // 1. Transfer all items from source to target
    orderItemDao.transferItems(sourceId, targetId)
    // 2. Delete the source group
    val sourceGroup = customerGroupDao.getGroupById(sourceId)
    if (sourceGroup != null) {
      customerGroupDao.deleteGroup(sourceGroup)
    }
  }

  override fun getOtherActiveGroups(excludeGroupId: Long): Flow<List<CustomerGroupEntity>> {
    return customerGroupDao.getGroupsByStatus("Active").map { groups ->
      groups.filter { it.id != excludeGroupId }
    }
  }

  override suspend fun updateGroupStatus(
    groupId: Long,
    status: String,
  ) {
    customerGroupDao.updateGroupStatus(groupId, status)
  }
}
