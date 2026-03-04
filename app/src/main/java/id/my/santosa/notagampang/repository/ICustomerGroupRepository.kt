package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.entity.CustomerGroupEntity
import kotlinx.coroutines.flow.Flow

interface ICustomerGroupRepository {
  fun getActiveGroupsWithTotals(): Flow<List<CustomerGroupWithTotal>>

  fun getInactiveGroupsWithTotals(): Flow<List<CustomerGroupWithTotal>>

  suspend fun getGroupById(groupId: Long): CustomerGroupEntity?

  fun getGroupFlowById(groupId: Long): Flow<CustomerGroupEntity?>

  suspend fun createNewGroup(alias: String): Long

  suspend fun updateGroup(group: CustomerGroupEntity)

  suspend fun deleteGroup(groupId: Long)

  suspend fun deletePaidGroups()

  suspend fun mergeGroups(
    sourceId: Long,
    targetId: Long,
  )

  fun getOtherActiveGroups(excludeGroupId: Long): Flow<List<CustomerGroupEntity>>

  suspend fun updateGroupStatus(
    groupId: Long,
    status: String,
  )
}
