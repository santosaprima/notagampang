package id.my.santosa.notagampang.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.my.santosa.notagampang.database.entity.DebtPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtPaymentDao {
  @Query("SELECT * FROM debt_payments WHERE debtRecordId = :debtRecordId ORDER BY timestamp DESC")
  fun getPaymentsForDebt(debtRecordId: Long): Flow<List<DebtPaymentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPayment(payment: DebtPaymentEntity): Long

  @Delete suspend fun deletePayment(payment: DebtPaymentEntity)
}
