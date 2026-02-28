package id.my.santosa.notagampang.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtRecordDao {
  @Query("SELECT * FROM debt_records ORDER BY timestamp DESC")
  fun getAllDebtRecords(): Flow<List<DebtRecordEntity>>

  @Query("SELECT * FROM debt_records WHERE status != 'Lunas' ORDER BY timestamp DESC")
  fun getActiveDebtRecords(): Flow<List<DebtRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDebtRecord(debtRecord: DebtRecordEntity): Long

  @Update suspend fun updateDebtRecord(debtRecord: DebtRecordEntity)

  @Delete suspend fun deleteDebtRecord(debtRecord: DebtRecordEntity)

  @Query("SELECT COALESCE(SUM(paidAmount), 0) FROM debt_records")
  fun getTotalKasbonIncome(): Flow<Int>

  @Query("SELECT COALESCE(SUM(remainingDebt), 0) FROM debt_records")
  fun getTotalActiveKasbon(): Flow<Int>
}
