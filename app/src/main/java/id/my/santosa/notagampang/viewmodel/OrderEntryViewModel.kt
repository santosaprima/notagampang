package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import id.my.santosa.notagampang.database.entity.OrderItemEntity
import id.my.santosa.notagampang.repository.MenuItemRepository
import id.my.santosa.notagampang.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OrderEntryUiState(
  val menuItems: List<MenuItemEntity> = emptyList(),
  val currentOrders: List<OrderItemEntity> = emptyList(),
  val selectedCategory: String = "Semua",
)

class OrderEntryViewModel(
  private val groupId: Long,
  private val menuRepository: MenuItemRepository,
  private val orderRepository: OrderRepository,
) : ViewModel() {
  private val selectedCategoryState = MutableStateFlow("Semua")

  val uiState: StateFlow<OrderEntryUiState> =
    combine(
      menuRepository.getAllMenuItems(),
      orderRepository.getOrdersForGroup(groupId),
      selectedCategoryState,
    ) { menu, orders, category ->
      val filteredMenu =
        if (category == "Semua") {
          menu
        } else {
          menu.filter { it.category == category }
        }
      OrderEntryUiState(
        menuItems = filteredMenu,
        currentOrders = orders,
        selectedCategory = category,
      )
    }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OrderEntryUiState(),
      )

  fun setCategory(category: String) {
    selectedCategoryState.value = category
  }

  fun addItemToOrder(menuItem: MenuItemEntity) {
    viewModelScope.launch {
      // Find if already exists in unpaid orders for this group
      val existing =
        uiState.value.currentOrders.find {
          it.menuItemId == menuItem.id && it.status == "Unpaid"
        }

      if (existing != null) {
        orderRepository.updateOrderItem(
          existing.copy(quantity = existing.quantity + 1),
        )
      } else {
        orderRepository.insertOrderItem(
          OrderItemEntity(
            customerGroupId = groupId,
            menuItemId = menuItem.id,
            customName = null,
            priceAtOrder = menuItem.price,
            quantity = 1,
            timestamp = System.currentTimeMillis(),
            status = "Unpaid",
          ),
        )
      }
    }
  }

  fun addCustomItem(
    name: String,
    price: Int,
  ) {
    viewModelScope.launch {
      orderRepository.insertOrderItem(
        OrderItemEntity(
          customerGroupId = groupId,
          menuItemId = null,
          customName = name,
          priceAtOrder = price,
          quantity = 1,
          timestamp = System.currentTimeMillis(),
          status = "Unpaid",
        ),
      )
    }
  }
}

class OrderEntryViewModelFactory(
  private val groupId: Long,
  private val menuRepository: MenuItemRepository,
  private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(OrderEntryViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return OrderEntryViewModel(groupId, menuRepository, orderRepository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
