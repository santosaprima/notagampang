package id.my.santosa.notagampang.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debt_records")
data class DebtRecordEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val customerName: String,
  val customerPhone: String?,
  val totalAmount: Int,
  val paidAmount: Int,
  val remainingDebt: Int,
  // Unpaid, PartiallyPaid, Lunas
  val status: String,
  val timestamp: Long,
)
