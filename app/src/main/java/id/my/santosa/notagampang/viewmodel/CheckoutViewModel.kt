package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.database.entity.CustomerGroupEntity
import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import id.my.santosa.notagampang.database.entity.OrderItemEntity
import id.my.santosa.notagampang.repository.CustomerGroupRepository
import id.my.santosa.notagampang.repository.DebtRecordRepository
import id.my.santosa.notagampang.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckoutUiState(
        val group: CustomerGroupEntity? = null,
        val unpaidItems: List<OrderItemEntity> = emptyList(),
        val selectedItemIds: Set<Long> = emptySet(),
        val isLoading: Boolean = true,
        val checkoutComplete: Boolean = false,
)

class CheckoutViewModel(
        private val groupId: Long,
        private val orderRepository: OrderRepository,
        private val customerGroupRepository: CustomerGroupRepository,
        private val debtRecordRepository: DebtRecordRepository,
) : ViewModel() {
  private val selectedItemIdsState = MutableStateFlow<Set<Long>>(emptySet())
  private val groupState = MutableStateFlow<CustomerGroupEntity?>(null)
  private val checkoutCompleteState = MutableStateFlow(false)

  val uiState: StateFlow<CheckoutUiState> =
          combine(
                          groupState,
                          orderRepository.getOrdersForGroup(groupId),
                          selectedItemIdsState,
                          checkoutCompleteState,
                  ) {
                          group: CustomerGroupEntity?,
                          items: List<OrderItemEntity>,
                          selectedIds: Set<Long>,
                          isComplete: Boolean,
                    ->
                    val unpaidItems = items.filter { it.status == "Unpaid" }
                    CheckoutUiState(
                            group = group,
                            unpaidItems = unpaidItems,
                            selectedItemIds = selectedIds,
                            isLoading = false,
                            checkoutComplete = isComplete,
                    )
                  }
                  .stateIn(
                          scope = viewModelScope,
                          started = SharingStarted.WhileSubscribed(5000),
                          initialValue = CheckoutUiState(),
                  )

  init {
    viewModelScope.launch {
      val loadedGroup = customerGroupRepository.getGroupById(groupId)
      groupState.value = loadedGroup
    }
  }

  fun toggleItemSelection(itemId: Long) {
    selectedItemIdsState.update { current ->
      if (current.contains(itemId)) current - itemId else current + itemId
    }
  }

  fun selectAll() {
    val allIds = uiState.value.unpaidItems.map { it.id }.toSet()
    selectedItemIdsState.value = allIds
  }

  fun clearSelection() {
    selectedItemIdsState.value = emptySet()
  }

  fun processCheckout(
          cashReceived: Int,
          customerName: String,
          customerPhone: String,
  ) {
    viewModelScope.launch {
      val state = uiState.value
      if (state.selectedItemIds.isEmpty()) return@launch

      val itemsToPay = state.unpaidItems.filter { state.selectedItemIds.contains(it.id) }
      val totalToPay = itemsToPay.sumOf { it.priceAtOrder * it.quantity }

      // 1. Mark selected items as Paid
      val paidItems = itemsToPay.map { it.copy(status = "Paid") }
      orderRepository.updateOrderItems(paidItems)

      // 2. Handle Debt (Kasbon) if cash is insufficient
      if (cashReceived < totalToPay) {
        val remainingDebt = totalToPay - cashReceived
        val debtRecord =
                DebtRecordEntity(
                        customerName = customerName.ifBlank { state.group?.alias ?: "Unknown" },
                        customerPhone = customerPhone.ifBlank { null },
                        totalAmount = totalToPay,
                        paidAmount = cashReceived,
                        remainingDebt = remainingDebt,
                        status = "Unpaid",
                        timestamp = System.currentTimeMillis(),
                )
        debtRecordRepository.insertDebtRecord(debtRecord)
      }

      // 3. Mark the group as Paid if all items are now paid
      val remainingUnpaidCount = state.unpaidItems.size - itemsToPay.size
      if (remainingUnpaidCount == 0 && state.group != null) {
        customerGroupRepository.updateGroup(state.group.copy(status = "Paid"))
      }

      checkoutCompleteState.value = true
    }
  }
}

class CheckoutViewModelFactory(
        private val groupId: Long,
        private val orderRepository: OrderRepository,
        private val customerGroupRepository: CustomerGroupRepository,
        private val debtRecordRepository: DebtRecordRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(CheckoutViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return CheckoutViewModel(
              groupId,
              orderRepository,
              customerGroupRepository,
              debtRecordRepository,
      ) as
              T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
