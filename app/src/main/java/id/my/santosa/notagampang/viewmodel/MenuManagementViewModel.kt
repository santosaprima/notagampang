package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import id.my.santosa.notagampang.repository.MenuItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MenuManagementViewModel(
  private val repository: MenuItemRepository,
) : ViewModel() {
  val menuItems: StateFlow<List<MenuItemEntity>> =
    repository
      .getAllMenuItems()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
      )

  fun addMenuItem(
    name: String,
    price: Int,
    category: String,
  ) {
    if (name.isNotBlank() && price > 0) {
      viewModelScope.launch {
        repository.insertMenuItem(
          MenuItemEntity(name = name.trim(), price = price, category = category),
        )
      }
    }
  }

  fun deleteMenuItem(menuItem: MenuItemEntity) {
    viewModelScope.launch { repository.deleteMenuItem(menuItem) }
  }
}

class MenuManagementViewModelFactory(
  private val repository: MenuItemRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MenuManagementViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return MenuManagementViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
