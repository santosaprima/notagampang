package id.my.santosa.notagampang.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_groups")
data class CustomerGroupEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val alias: String,
  val createdAt: Long,
  // Active, CheckoutInitiated, Paid
  val status: String,
)
