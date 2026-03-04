package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.repository.ICustomerGroupRepository
import id.my.santosa.notagampang.repository.IDebtRecordRepository
import id.my.santosa.notagampang.repository.IOrderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ShiftManagementUiState(
  val totalPaidIncome: Int = 0,
  val totalKasbonIncome: Int = 0,
  val totalActiveKasbon: Int = 0,
) {
  val totalIncome: Int
    get() = totalPaidIncome + totalKasbonIncome
}

class ShiftManagementViewModel(
  private val orderRepository: IOrderRepository,
  private val customerGroupRepository: ICustomerGroupRepository,
  private val debtRecordRepository: IDebtRecordRepository,
) : ViewModel() {
  val uiState: StateFlow<ShiftManagementUiState> =
    combine(
      orderRepository.getTotalPaidIncome(),
      debtRecordRepository.getTotalKasbonIncome(),
      debtRecordRepository.getTotalActiveKasbon(),
    ) { paidIncome, kasbonIncome, activeKasbon ->
      ShiftManagementUiState(
        totalPaidIncome = paidIncome,
        totalKasbonIncome = kasbonIncome,
        totalActiveKasbon = activeKasbon,
      )
    }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShiftManagementUiState(),
      )

  fun closeShift(onComplete: () -> Unit) {
    viewModelScope.launch {
      // Clear closed tabs and orders
      customerGroupRepository.deletePaidGroups()
      onComplete()
    }
  }
}

class ShiftManagementViewModelFactory(
  private val orderRepository: IOrderRepository,
  private val customerGroupRepository: ICustomerGroupRepository,
  private val debtRecordRepository: IDebtRecordRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ShiftManagementViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return ShiftManagementViewModel(
        orderRepository,
        customerGroupRepository,
        debtRecordRepository,
      ) as
        T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
