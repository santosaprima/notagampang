package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.repository.CustomerGroupRepository
import id.my.santosa.notagampang.repository.CustomerGroupWithTotal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FloatingTabsViewModel(
  private val repository: CustomerGroupRepository,
) : ViewModel() {
  val activeGroups: StateFlow<List<CustomerGroupWithTotal>> =
    repository
      .getActiveGroupsWithTotals()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
      )

  fun createNewTab(alias: String) {
    if (alias.isNotBlank()) {
      viewModelScope.launch { repository.createNewGroup(alias.trim()) }
    }
  }
}

class FloatingTabsViewModelFactory(
  private val repository: CustomerGroupRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(FloatingTabsViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return FloatingTabsViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
