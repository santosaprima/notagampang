package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.DebtRecordDao
import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import kotlinx.coroutines.flow.Flow

class DebtRecordRepository(private val debtRecordDao: DebtRecordDao) {
  fun getAllDebtRecords(): Flow<List<DebtRecordEntity>> = debtRecordDao.getAllDebtRecords()

  fun getActiveDebtRecords(): Flow<List<DebtRecordEntity>> = debtRecordDao.getActiveDebtRecords()

  suspend fun insertDebtRecord(record: DebtRecordEntity): Long = debtRecordDao.insertDebtRecord(record)

  suspend fun updateDebtRecord(record: DebtRecordEntity) = debtRecordDao.updateDebtRecord(record)

  suspend fun deleteDebtRecord(record: DebtRecordEntity) = debtRecordDao.deleteDebtRecord(record)

  fun getTotalKasbonIncome(): Flow<Int> = debtRecordDao.getTotalKasbonIncome()

  fun getTotalActiveKasbon(): Flow<Int> = debtRecordDao.getTotalActiveKasbon()
}
