package id.my.santosa.notagampang.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val price: Int,
  // Minuman, Makanan, Sate, Snack
  val category: String,
  // For visual representation
  val colorHex: String? = null,
)
