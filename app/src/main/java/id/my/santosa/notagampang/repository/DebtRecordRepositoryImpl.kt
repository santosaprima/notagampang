package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.DebtRecordDao
import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import kotlinx.coroutines.flow.Flow

class DebtRecordRepositoryImpl(
  private val debtRecordDao: DebtRecordDao,
  private val debtPaymentDao: id.my.santosa.notagampang.database.dao.DebtPaymentDao,
) : IDebtRecordRepository {
  override fun getAllDebtRecords(): Flow<List<DebtRecordEntity>> = debtRecordDao.getAllDebtRecords()

  override fun getActiveDebtRecords(): Flow<List<DebtRecordEntity>> = debtRecordDao.getActiveDebtRecords()

  override suspend fun insertDebtRecord(record: DebtRecordEntity): Long = debtRecordDao.insertDebtRecord(record)

  override suspend fun updateDebtRecord(record: DebtRecordEntity) = debtRecordDao.updateDebtRecord(record)

  override suspend fun deleteDebtRecord(record: DebtRecordEntity) = debtRecordDao.deleteDebtRecord(record)

  override fun getTotalKasbonIncome(): Flow<Int> = debtRecordDao.getTotalKasbonIncome()

  override fun getTotalActiveKasbon(): Flow<Int> = debtRecordDao.getTotalActiveKasbon()

  override suspend fun insertDebtPayment(payment: id.my.santosa.notagampang.database.entity.DebtPaymentEntity): Long =
    debtPaymentDao.insertPayment(payment)

  override fun getPaymentsForDebt(debtRecordId: Long): Flow<List<id.my.santosa.notagampang.database.entity.DebtPaymentEntity>> =
    debtPaymentDao.getPaymentsForDebt(debtRecordId)
}
