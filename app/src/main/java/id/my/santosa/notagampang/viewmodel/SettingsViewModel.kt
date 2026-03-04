package id.my.santosa.notagampang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import id.my.santosa.notagampang.data.PreferenceManager
import id.my.santosa.notagampang.data.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val preferenceManager: PreferenceManager) : ViewModel() {
  val themeMode: StateFlow<ThemeMode> =
    preferenceManager.themeMode.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = ThemeMode.SYSTEM,
    )
  val whatsappPrompt: StateFlow<String> =
    preferenceManager.whatsappPrompt.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = PreferenceManager.DEFAULT_WHATSAPP_PROMPT,
    )

  fun setThemeMode(mode: ThemeMode) {
    viewModelScope.launch { preferenceManager.setThemeMode(mode) }
  }

  fun setWhatsappPrompt(prompt: String) {
    viewModelScope.launch { preferenceManager.setWhatsappPrompt(prompt) }
  }
}

class SettingsViewModelFactory(private val preferenceManager: PreferenceManager) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return SettingsViewModel(preferenceManager) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
