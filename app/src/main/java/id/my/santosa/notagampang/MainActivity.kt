package id.my.santosa.notagampang

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.santosa.notagampang.database.AppDatabase
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import id.my.santosa.notagampang.repository.CustomerGroupRepository
import id.my.santosa.notagampang.repository.DebtRecordRepository
import id.my.santosa.notagampang.repository.MenuItemRepository
import id.my.santosa.notagampang.repository.OrderRepository
import id.my.santosa.notagampang.ui.screen.CheckoutScreen
import id.my.santosa.notagampang.ui.screen.FloatingTabsScreen
import id.my.santosa.notagampang.ui.screen.KasbonScreen
import id.my.santosa.notagampang.ui.screen.MenuManagementScreen
import id.my.santosa.notagampang.ui.screen.OrderEntryScreen
import id.my.santosa.notagampang.ui.screen.ShiftManagementScreen
import id.my.santosa.notagampang.ui.screen.SuggestionPresetsScreen
import id.my.santosa.notagampang.viewmodel.CheckoutViewModel
import id.my.santosa.notagampang.viewmodel.CheckoutViewModelFactory
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModel
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModelFactory
import id.my.santosa.notagampang.viewmodel.KasbonViewModel
import id.my.santosa.notagampang.viewmodel.KasbonViewModelFactory
import id.my.santosa.notagampang.viewmodel.MenuManagementViewModel
import id.my.santosa.notagampang.viewmodel.MenuManagementViewModelFactory
import id.my.santosa.notagampang.viewmodel.OrderEntryViewModel
import id.my.santosa.notagampang.viewmodel.OrderEntryViewModelFactory
import id.my.santosa.notagampang.viewmodel.ShiftManagementViewModel
import id.my.santosa.notagampang.viewmodel.ShiftManagementViewModelFactory
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModel
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModelFactory
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

sealed class Screen {
  object FloatingTabs : Screen()

  object SuggestionPresets : Screen()

  object MenuManagement : Screen()

  data class OrderEntry(val groupId: Long) : Screen()

  data class Checkout(val groupId: Long) : Screen()

  object Kasbon : Screen()

  object ShiftManagement : Screen()
}

val Context.dataStore by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val database = AppDatabase.getDatabase(this)
    val groupRepository =
      CustomerGroupRepository(
        database.customerGroupDao(),
        database.orderItemDao(),
      )
    val presetDao = database.suggestionPresetDao()
    val menuRepository = MenuItemRepository(database.menuItemDao())
    val orderRepository = OrderRepository(database.orderItemDao())
    val debtRecordRepository =
      id.my.santosa.notagampang.repository.DebtRecordRepository(
        database.debtRecordDao(),
      )

    setContent {
      MaterialTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
          var currentScreen by remember { mutableStateOf<Screen>(Screen.FloatingTabs) }

          val floatingTabsViewModel: FloatingTabsViewModel =
            viewModel(
              factory =
                FloatingTabsViewModelFactory(
                  groupRepository,
                ),
            )

          val presetsViewModel: SuggestionPresetsViewModel =
            viewModel(
              factory =
                SuggestionPresetsViewModelFactory(
                  presetDao,
                ),
            )

          val menuManagementViewModel: MenuManagementViewModel =
            viewModel(
              factory =
                MenuManagementViewModelFactory(
                  menuRepository,
                ),
            )

          val isFirstRunFlow =
            dataStore.data.map { preferences ->
              preferences[booleanPreferencesKey("is_first_run")] ?: true
            }
          val isFirstRun by isFirstRunFlow.collectAsState(initial = null)

          LaunchedEffect(isFirstRun) {
            if (isFirstRun == true) {
              presetsViewModel.seedDefaults()
              seedDefaultMenu(menuRepository)
              dataStore.edit { preferences ->
                preferences[booleanPreferencesKey("is_first_run")] = false
              }
            }
          }

          val presets by
            presetsViewModel.presets.collectAsState(
              initial = emptyList(),
            )

          when (val screen = currentScreen) {
            is Screen.FloatingTabs -> {
              FloatingTabsScreen(
                viewModel = floatingTabsViewModel,
                suggestions = presets.map { it.label },
                onTabClick = { groupId ->
                  currentScreen =
                    Screen.OrderEntry(
                      groupId,
                    )
                },
                onKasbonClick = { currentScreen = Screen.Kasbon },
                onSettingsClick = { currentScreen = Screen.SuggestionPresets },
                onMenuSettingsClick = { currentScreen = Screen.MenuManagement },
                onShiftManagementClick = { currentScreen = Screen.ShiftManagement },
              )
            }
            is Screen.SuggestionPresets -> {
              SuggestionPresetsScreen(
                viewModel = presetsViewModel,
                onBack = { currentScreen = Screen.FloatingTabs },
              )
            }
            is Screen.MenuManagement -> {
              MenuManagementScreen(
                viewModel = menuManagementViewModel,
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
                      groupRepository,
                      menuRepository,
                      orderRepository,
                    ),
                )
              OrderEntryScreen(
                viewModel = orderEntryViewModel,
                onBack = { currentScreen = Screen.FloatingTabs },
                onCheckout = {
                  currentScreen =
                    Screen.Checkout(
                      screen.groupId,
                    )
                },
                onDeleteGroup = {
                  orderEntryViewModel.deleteGroup()
                  currentScreen = Screen.FloatingTabs
                },
              )
            }
            is Screen.Checkout -> {
              val checkoutViewModel: CheckoutViewModel =
                viewModel(
                  key = "Checkout_${screen.groupId}",
                  factory =
                    CheckoutViewModelFactory(
                      screen.groupId,
                      orderRepository,
                      groupRepository,
                      debtRecordRepository,
                    ),
                )
              CheckoutScreen(
                viewModel = checkoutViewModel,
                onBack = {
                  currentScreen =
                    Screen.OrderEntry(
                      screen.groupId,
                    )
                },
                onCheckoutComplete = { currentScreen = Screen.FloatingTabs },
              )
            }
            is Screen.Kasbon -> {
              val kasbonViewModel: KasbonViewModel =
                viewModel(factory = KasbonViewModelFactory(debtRecordRepository))
              KasbonScreen(
                viewModel = kasbonViewModel,
                onBack = { currentScreen = Screen.FloatingTabs },
              )
            }
            is Screen.ShiftManagement -> {
              val shiftManagementViewModel: ShiftManagementViewModel =
                viewModel(
                  factory =
                    ShiftManagementViewModelFactory(
                      orderRepository,
                      groupRepository,
                      debtRecordRepository,
                    ),
                )
              ShiftManagementScreen(
                viewModel = shiftManagementViewModel,
                onBack = { currentScreen = Screen.FloatingTabs },
              )
            }
          }
        }
      }
    }
  }

  private fun seedDefaultMenu(repository: MenuItemRepository) {
    val scope = kotlinx.coroutines.MainScope()
    scope.launch(kotlinx.coroutines.Dispatchers.IO) {
      if (repository.getCount() == 0) {
        val menu =
          listOf(
            MenuItemEntity(
              name = "Es Teh",
              price = 3000,
              category = "Minuman",
            ),
            MenuItemEntity(
              name = "Teh Hangat",
              price = 3000,
              category = "Minuman",
            ),
            MenuItemEntity(
              name = "Es Jeruk",
              price = 4000,
              category = "Minuman",
            ),
            MenuItemEntity(
              name = "Kopi Hitam",
              price = 4000,
              category = "Minuman",
            ),
            MenuItemEntity(
              name = "Sate Usus",
              price = 2000,
              category = "Sate",
            ),
            MenuItemEntity(
              name = "Sate Kulit",
              price = 2000,
              category = "Sate",
            ),
            MenuItemEntity(
              name = "Sate Telur Puyuh",
              price = 3000,
              category = "Sate",
            ),
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
            MenuItemEntity(
              name = "Nasi Kucing (Bandeng)",
              price = 3500,
              category = "Makanan",
            ),
            MenuItemEntity(
              name = "Singkong Goreng",
              price = 2000,
              category = "Snack",
            ),
            MenuItemEntity(
              name = "Tempe Mendoan",
              price = 1500,
              category = "Snack",
            ),
          )
        for (item in menu) {
          repository.insertMenuItem(item)
        }
      }
    }
  }
}
