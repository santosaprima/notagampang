package id.my.santosa.notagampang.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "order_items",
  foreignKeys =
    [
      ForeignKey(
        entity = CustomerGroupEntity::class,
        parentColumns = ["id"],
        childColumns = ["customerGroupId"],
        onDelete = ForeignKey.CASCADE,
      ),
      ForeignKey(
        entity = MenuItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["menuItemId"],
        // Keep the order even if menu item is deleted
        onDelete = ForeignKey.SET_NULL,
      ),
    ],
  indices = [Index(value = ["customerGroupId"]), Index(value = ["menuItemId"])],
)
data class OrderItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val customerGroupId: Long,
  // Nullable for "Item Bebas"
  val menuItemId: Long?,
  // Used if menuItemId is null
  val customName: String?,
  // Freeze the price when ordered
  val priceAtOrder: Int,
  val quantity: Int,
  val timestamp: Long,
  // Unpaid, Paid
  val status: String,
)
