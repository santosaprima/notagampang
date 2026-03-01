package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import id.my.santosa.notagampang.database.entity.OrderItemEntity
import id.my.santosa.notagampang.repository.CustomerGroupRepository
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
        val otherActiveGroups: List<id.my.santosa.notagampang.database.entity.CustomerGroupEntity> =
                emptyList(),
        val group: id.my.santosa.notagampang.database.entity.CustomerGroupEntity? = null,
)

class OrderEntryViewModel(
        private val groupId: Long,
        private val groupRepository: CustomerGroupRepository,
        private val menuRepository: MenuItemRepository,
        private val orderRepository: OrderRepository,
) : ViewModel() {
  private val selectedCategoryState = MutableStateFlow("Semua")

  val uiState: StateFlow<OrderEntryUiState> =
          combine(
                          menuRepository.getAllMenuItems(),
                          orderRepository.getOrdersForGroup(groupId),
                          selectedCategoryState,
                          groupRepository.getOtherActiveGroups(groupId),
                          groupRepository.getGroupFlowById(groupId),
                  ) { menu, orders, category, otherGroups, group ->
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
                            otherActiveGroups = otherGroups,
                            group = group,
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
                        customName = menuItem.name,
                        priceAtOrder = menuItem.price,
                        quantity = 1,
                        timestamp = System.currentTimeMillis(),
                        status = "Unpaid",
                ),
        )
      }
    }
  }

  fun removeItemFromOrder(menuItemId: Long) {
    viewModelScope.launch {
      val existing =
              uiState.value.currentOrders.find {
                it.menuItemId == menuItemId && it.status == "Unpaid"
              }
      if (existing != null) {
        if (existing.quantity > 1) {
          orderRepository.updateOrderItem(
                  existing.copy(quantity = existing.quantity - 1),
          )
        } else {
          orderRepository.deleteOrder(existing)
        }
      }
    }
  }

  fun addCustomItem(
          name: String,
          price: Int,
  ) {
    viewModelScope.launch {
      val existing =
              uiState.value.currentOrders.find {
                it.menuItemId == null &&
                        it.customName == name &&
                        it.priceAtOrder == price &&
                        it.status == "Unpaid"
              }

      if (existing != null) {
        orderRepository.updateOrderItem(
                existing.copy(quantity = existing.quantity + 1),
        )
      } else {
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

  fun deleteGroup() {
    viewModelScope.launch { groupRepository.deleteGroup(groupId) }
  }

  fun mergeWithOtherGroup(targetGroupId: Long) {
    viewModelScope.launch { groupRepository.mergeGroups(groupId, targetGroupId) }
  }
}

class OrderEntryViewModelFactory(
        private val groupId: Long,
        private val groupRepository: CustomerGroupRepository,
        private val menuRepository: MenuItemRepository,
        private val orderRepository: OrderRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(OrderEntryViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return OrderEntryViewModel(groupId, groupRepository, menuRepository, orderRepository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
