package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import kotlinx.coroutines.flow.Flow

interface IDebtRecordRepository {
  fun getAllDebtRecords(): Flow<List<DebtRecordEntity>>

  fun getActiveDebtRecords(): Flow<List<DebtRecordEntity>>

  suspend fun insertDebtRecord(record: DebtRecordEntity): Long

  suspend fun updateDebtRecord(record: DebtRecordEntity)

  suspend fun deleteDebtRecord(record: DebtRecordEntity)

  fun getTotalKasbonIncome(): Flow<Int>

  fun getTotalActiveKasbon(): Flow<Int>

  suspend fun insertDebtPayment(payment: id.my.santosa.notagampang.database.entity.DebtPaymentEntity): Long

  fun getPaymentsForDebt(debtRecordId: Long): Flow<List<id.my.santosa.notagampang.database.entity.DebtPaymentEntity>>
}
