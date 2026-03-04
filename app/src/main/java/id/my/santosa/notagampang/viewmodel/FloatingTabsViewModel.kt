package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.repository.CustomerGroupRepository
import id.my.santosa.notagampang.repository.CustomerGroupWithTotal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FloatingTabsViewModel(
  private val repository: CustomerGroupRepository,
) : ViewModel() {
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery

  private val _selectedTab = MutableStateFlow(0) // 0: Aktif, 1: Selesai
  val selectedTab: StateFlow<Int> = _selectedTab

  val filteredGroups: StateFlow<List<CustomerGroupWithTotal>> =
    combine(
      _selectedTab,
      _searchQuery,
      repository.getActiveGroupsWithTotals(),
      repository.getInactiveGroupsWithTotals(),
    ) { tab, query, active, inactive ->
      val groups = if (tab == 0) active else inactive
      if (query.isEmpty()) {
        groups
      } else {
        groups.filter { it.group.alias.contains(query, ignoreCase = true) }
      }
    }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
      )

  fun onTabSelected(tabIndex: Int) {
    _selectedTab.value = tabIndex
  }

  fun onSearchQueryChange(newQuery: String) {
    _searchQuery.value = newQuery
  }

  fun createNewTab(alias: String) {
    if (alias.isNotBlank()) {
      viewModelScope.launch { repository.createNewGroup(alias.trim()) }
    }
  }

  fun mergeGroups(
    sourceId: Long,
    targetId: Long,
  ) {
    viewModelScope.launch { repository.mergeGroups(sourceId, targetId) }
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
