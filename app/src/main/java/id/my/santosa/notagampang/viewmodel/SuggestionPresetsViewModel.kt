package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.database.entity.SuggestionPresetEntity
import id.my.santosa.notagampang.repository.ISuggestionPresetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SuggestionPresetsViewModel(
  private val repository: ISuggestionPresetRepository,
) : ViewModel() {
  val presets: StateFlow<List<SuggestionPresetEntity>> =
    repository
      .getAllPresets()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
      )

  fun addPreset(label: String) {
    if (label.isNotBlank()) {
      viewModelScope.launch {
        repository.insertPreset(SuggestionPresetEntity(label = label.trim()))
      }
    }
  }

  fun deletePreset(preset: SuggestionPresetEntity) {
    viewModelScope.launch { repository.deletePreset(preset) }
  }

  fun seedDefaults() {
    viewModelScope.launch {
      if (repository.getCount() == 0) {
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
          repository.insertPreset(SuggestionPresetEntity(label = label, sortOrder = index))
        }
      }
    }
  }
}

class SuggestionPresetsViewModelFactory(
  private val repository: ISuggestionPresetRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SuggestionPresetsViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return SuggestionPresetsViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
