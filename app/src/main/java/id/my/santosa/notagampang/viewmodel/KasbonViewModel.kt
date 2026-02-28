package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import id.my.santosa.notagampang.repository.DebtRecordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class KasbonUiState(
  val activeDebts: List<DebtRecordEntity> = emptyList(),
  val isLoading: Boolean = true,
)

class KasbonViewModel(private val debtRecordRepository: DebtRecordRepository) : ViewModel() {
  val uiState: StateFlow<KasbonUiState> =
    debtRecordRepository
      .getActiveDebtRecords()
      .map { KasbonUiState(activeDebts = it, isLoading = false) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = KasbonUiState(),
      )

  fun receiveInstallment(
    record: DebtRecordEntity,
    amount: Int,
  ) {
    viewModelScope.launch {
      val newRemaining = record.remainingDebt - amount
      val newPaid = record.paidAmount + amount
      val newStatus = if (newRemaining <= 0) "Lunas" else "PartiallyPaid"
      debtRecordRepository.updateDebtRecord(
        record.copy(
          remainingDebt = maxOf(0, newRemaining),
          paidAmount = newPaid,
          status = newStatus,
        ),
      )
    }
  }
}

class KasbonViewModelFactory(private val debtRecordRepository: DebtRecordRepository) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(KasbonViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return KasbonViewModel(debtRecordRepository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
