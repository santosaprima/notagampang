package id.my.santosa.notagampang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.santosa.notagampang.database.AppDatabase
import id.my.santosa.notagampang.repository.CustomerGroupRepository
import id.my.santosa.notagampang.ui.screen.FloatingTabsScreen
import id.my.santosa.notagampang.ui.screen.SuggestionPresetsScreen
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModel
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModelFactory
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModel
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModelFactory

enum class Screen {
  FloatingTabs,
  SuggestionPresets,
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val database = AppDatabase.getDatabase(this)
    val repository = CustomerGroupRepository(database.customerGroupDao(), database.orderItemDao())
    val presetDao = database.suggestionPresetDao()

    setContent {
      MaterialTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
          var currentScreen by remember { mutableStateOf(Screen.FloatingTabs) }

          val floatingTabsViewModel: FloatingTabsViewModel =
            viewModel(factory = FloatingTabsViewModelFactory(repository))

          val presetsViewModel: SuggestionPresetsViewModel =
            viewModel(factory = SuggestionPresetsViewModelFactory(presetDao))

          // Seed defaults if empty
          presetsViewModel.seedDefaults()

          val presets by presetsViewModel.presets.collectAsState(initial = emptyList())

          when (currentScreen) {
            Screen.FloatingTabs -> {
              FloatingTabsScreen(
                viewModel = floatingTabsViewModel,
                suggestions = presets.map { it.label },
                onTabClick = { groupId ->
                  // TODO: Navigate to Order Entry Screen
                },
                onSettingsClick = { currentScreen = Screen.SuggestionPresets },
              )
            }
            Screen.SuggestionPresets -> {
              SuggestionPresetsScreen(
                viewModel = presetsViewModel,
                onBack = { currentScreen = Screen.FloatingTabs },
              )
            }
          }
        }
      }
    }
  }
}
