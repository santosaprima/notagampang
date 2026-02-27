package id.my.santosa.notagampang.database.dao

import androidx.room.Embedded
import id.my.santosa.notagampang.database.entity.CustomerGroupEntity

data class CustomerGroupWithTotalDaoModel(
  @Embedded val group: CustomerGroupEntity,
  val totalUnpaid: Int,
)
