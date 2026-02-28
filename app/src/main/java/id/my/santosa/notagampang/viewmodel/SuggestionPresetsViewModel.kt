package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.database.dao.SuggestionPresetDao
import id.my.santosa.notagampang.database.entity.SuggestionPresetEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SuggestionPresetsViewModel(
  private val dao: SuggestionPresetDao,
) : ViewModel() {
  val presets: StateFlow<List<SuggestionPresetEntity>> =
    dao.getAllPresets()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
      )

  fun addPreset(label: String) {
    if (label.isNotBlank()) {
      viewModelScope.launch { dao.insertPreset(SuggestionPresetEntity(label = label.trim())) }
    }
  }

  fun deletePreset(preset: SuggestionPresetEntity) {
    viewModelScope.launch { dao.deletePreset(preset) }
  }

  fun seedDefaults() {
    viewModelScope.launch {
      if (dao.getCount() == 0) {
        val defaults =
          listOf(
            "Bungkus",
            "Makan Sini",
            "Bungkus",
            "Topi",
            "Jaket",
            "Helm",
            "Kacamata",
            "Peci",
            "Ojol",
            "Gojek",
            "Grab",
            "ShopeeFood",
            "Rombongan",
            "Pojok",
            "Lesehan",
          )
        defaults.forEachIndexed { index, label ->
          dao.insertPreset(SuggestionPresetEntity(label = label, sortOrder = index))
        }
      }
    }
  }
}

class SuggestionPresetsViewModelFactory(
  private val dao: SuggestionPresetDao,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SuggestionPresetsViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return SuggestionPresetsViewModel(dao) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
