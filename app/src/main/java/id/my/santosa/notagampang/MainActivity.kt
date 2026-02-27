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
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import id.my.santosa.notagampang.repository.CustomerGroupRepository
import id.my.santosa.notagampang.repository.MenuItemRepository
import id.my.santosa.notagampang.repository.OrderRepository
import id.my.santosa.notagampang.ui.screen.FloatingTabsScreen
import id.my.santosa.notagampang.ui.screen.OrderEntryScreen
import id.my.santosa.notagampang.ui.screen.SuggestionPresetsScreen
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModel
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModelFactory
import id.my.santosa.notagampang.viewmodel.OrderEntryViewModel
import id.my.santosa.notagampang.viewmodel.OrderEntryViewModelFactory
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModel
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModelFactory
import kotlinx.coroutines.launch

sealed class Screen {
  object FloatingTabs : Screen()

  object SuggestionPresets : Screen()

  data class OrderEntry(val groupId: Long) : Screen()
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val database = AppDatabase.getDatabase(this)
    val groupRepository =
      CustomerGroupRepository(database.customerGroupDao(), database.orderItemDao())
    val presetDao = database.suggestionPresetDao()
    val menuRepository = MenuItemRepository(database.menuItemDao())
    val orderRepository = OrderRepository(database.orderItemDao())

    setContent {
      MaterialTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
          var currentScreen by remember { mutableStateOf<Screen>(Screen.FloatingTabs) }

          val floatingTabsViewModel: FloatingTabsViewModel =
            viewModel(factory = FloatingTabsViewModelFactory(groupRepository))

          val presetsViewModel: SuggestionPresetsViewModel =
            viewModel(factory = SuggestionPresetsViewModelFactory(presetDao))

          // Seed defaults if empty
          presetsViewModel.seedDefaults()
          seedDefaultMenu(menuRepository)

          val presets by presetsViewModel.presets.collectAsState(initial = emptyList())

          when (val screen = currentScreen) {
            is Screen.FloatingTabs -> {
              FloatingTabsScreen(
                viewModel = floatingTabsViewModel,
                suggestions = presets.map { it.label },
                onTabClick = { groupId -> currentScreen = Screen.OrderEntry(groupId) },
                onSettingsClick = { currentScreen = Screen.SuggestionPresets },
              )
            }
            is Screen.SuggestionPresets -> {
              SuggestionPresetsScreen(
                viewModel = presetsViewModel,
                onBack = { currentScreen = Screen.FloatingTabs },
              )
            }
            is Screen.OrderEntry -> {
              val orderEntryViewModel: OrderEntryViewModel =
                viewModel(
                  key = "OrderEntry_${screen.groupId}",
                  factory =
                    OrderEntryViewModelFactory(
                      screen.groupId,
                      menuRepository,
                      orderRepository,
                    ),
                )
              OrderEntryScreen(
                viewModel = orderEntryViewModel,
                onBack = { currentScreen = Screen.FloatingTabs },
                onCheckout = {
                  // TODO: Navigate to Checkout Screen
                },
              )
            }
          }
        }
      }
    }
  }

  private fun seedDefaultMenu(repository: MenuItemRepository) {
    val scope = kotlinx.coroutines.MainScope()
    scope.launch {
      val menu =
        listOf(
          MenuItemEntity(name = "Es Teh", price = 3000, category = "Minuman"),
          MenuItemEntity(name = "Teh Hangat", price = 3000, category = "Minuman"),
          MenuItemEntity(name = "Es Jeruk", price = 4000, category = "Minuman"),
          MenuItemEntity(name = "Kopi Hitam", price = 4000, category = "Minuman"),
          MenuItemEntity(name = "Sate Usus", price = 2000, category = "Sate"),
          MenuItemEntity(name = "Sate Kulit", price = 2000, category = "Sate"),
          MenuItemEntity(name = "Sate Telur Puyuh", price = 3000, category = "Sate"),
          MenuItemEntity(
            name = "Nasi Kucing (Teri)",
            price = 3000,
            category = "Makanan",
          ),
          MenuItemEntity(
            name = "Nasi Kucing (Sambal)",
            price = 3000,
            category = "Makanan",
          ),
          MenuItemEntity(name = "Gorengan", price = 1000, category = "Snack"),
        )
      menu.forEach { repository.insertMenuItem(it) }
    }
  }
}
