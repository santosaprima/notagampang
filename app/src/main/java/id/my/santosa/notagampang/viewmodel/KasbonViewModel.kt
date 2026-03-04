package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.database.entity.DebtPaymentEntity
import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import id.my.santosa.notagampang.repository.CustomerGroupRepository
import id.my.santosa.notagampang.repository.DebtRecordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class KasbonUiState(
  val activeDebts: List<DebtRecordEntity> = emptyList(),
  val completedDebts: List<DebtRecordEntity> = emptyList(),
  val payments: Map<Long, List<DebtPaymentEntity>> = emptyMap(),
  /** 0: Aktif, 1: Selesai */
  val selectedTab: Int = 0,
  val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
class KasbonViewModel(
  private val debtRecordRepository: DebtRecordRepository,
  private val customerGroupRepository: CustomerGroupRepository,
  private val preferenceManager: id.my.santosa.notagampang.data.PreferenceManager,
) : ViewModel() {
  private val selectedTabState = MutableStateFlow(0)
  private val searchQueryState = MutableStateFlow("")
  val searchQuery: StateFlow<String> = searchQueryState.asStateFlow()

  val whatsappPrompt: StateFlow<String> =
    preferenceManager.whatsappPrompt.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue =
        id.my.santosa.notagampang.data.PreferenceManager.DEFAULT_WHATSAPP_PROMPT,
    )

  val uiState: StateFlow<KasbonUiState> =
    combine(
      debtRecordRepository.getAllDebtRecords(),
      selectedTabState,
      debtRecordRepository.getAllDebtRecords().flatMapLatest { records ->
        val paymentFlows =
          records.map {
            debtRecordRepository.getPaymentsForDebt(
              it.id,
            )
          }
        if (paymentFlows.isEmpty()) {
          flowOf(emptyList<List<DebtPaymentEntity>>())
        } else {
          combine(paymentFlows) { it.toList() }
        }
      },
      searchQueryState,
    ) {
        allDebts: List<DebtRecordEntity>,
        selectedTab: Int,
        allPaymentsLists: List<List<DebtPaymentEntity>>,
        query: String,
      ->
      val filteredDebts =
        if (query.isEmpty()) {
          allDebts
        } else {
          allDebts.filter {
            it.customerName.contains(
              query,
              ignoreCase = true,
            )
          }
        }

      val active = filteredDebts.filter { it.status != "Lunas" }
      val completed = filteredDebts.filter { it.status == "Lunas" }

      val currentDebts = if (selectedTab == 0) active else completed

      val paymentsMap =
        currentDebts.associate { debt ->
          debt.id to
            allPaymentsLists.flatten().filter {
              it.debtRecordId == debt.id
            }
        }
      KasbonUiState(
        activeDebts = active,
        completedDebts = completed,
        payments = paymentsMap,
        selectedTab = selectedTab,
        isLoading = false,
      )
    }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = KasbonUiState(),
      )

  fun onTabSelected(tabIndex: Int) {
    selectedTabState.value = tabIndex
  }

  fun onSearchQueryChange(query: String) {
    searchQueryState.value = query
  }

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
      if (newRemaining <= 0) {
        record.groupId?.let { groupId ->
          customerGroupRepository.updateGroupStatus(groupId, "Paid")
        }
      }
      debtRecordRepository.insertDebtPayment(
        DebtPaymentEntity(
          debtRecordId = record.id,
          amount = amount,
          timestamp = System.currentTimeMillis(),
        ),
      )
    }
  }
}

class KasbonViewModelFactory(
  private val debtRecordRepository: DebtRecordRepository,
  private val customerGroupRepository: CustomerGroupRepository,
  private val preferenceManager: id.my.santosa.notagampang.data.PreferenceManager,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(KasbonViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return KasbonViewModel(
        debtRecordRepository,
        customerGroupRepository,
        preferenceManager,
      ) as
        T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
