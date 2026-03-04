package id.my.santosa.notagampang.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "debt_payments",
  foreignKeys =
    [
      ForeignKey(
        entity = DebtRecordEntity::class,
        parentColumns = ["id"],
        childColumns = ["debtRecordId"],
        onDelete = ForeignKey.CASCADE,
      ),
    ],
  indices = [Index(value = ["debtRecordId"])],
)
data class DebtPaymentEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val debtRecordId: Long,
  val amount: Int,
  val timestamp: Long,
)
