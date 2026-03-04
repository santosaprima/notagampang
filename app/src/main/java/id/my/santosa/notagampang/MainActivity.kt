package id.my.santosa.notagampang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ManageHistory
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.santosa.notagampang.data.ThemeMode
import id.my.santosa.notagampang.database.entity.CategoryEntity
import id.my.santosa.notagampang.database.entity.CustomerGroupEntity
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import id.my.santosa.notagampang.database.entity.SuggestionPresetEntity
import id.my.santosa.notagampang.repository.ICategoryRepository
import id.my.santosa.notagampang.repository.IMenuItemRepository
import id.my.santosa.notagampang.repository.ISuggestionPresetRepository
import id.my.santosa.notagampang.ui.screen.CheckoutScreen
import id.my.santosa.notagampang.ui.screen.FloatingTabsScreen
import id.my.santosa.notagampang.ui.screen.KasbonScreen
import id.my.santosa.notagampang.ui.screen.ManagementScreen
import id.my.santosa.notagampang.ui.screen.OrderEntryScreen
import id.my.santosa.notagampang.ui.screen.SettingsScreen
import id.my.santosa.notagampang.ui.screen.ShiftManagementScreen
import id.my.santosa.notagampang.ui.theme.NotaGampangTheme
import id.my.santosa.notagampang.ui.util.PriorityBackHandler
import id.my.santosa.notagampang.viewmodel.CategoryManagementViewModel
import id.my.santosa.notagampang.viewmodel.CategoryManagementViewModelFactory
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
import id.my.santosa.notagampang.viewmodel.SettingsViewModel
import id.my.santosa.notagampang.viewmodel.SettingsViewModelFactory
import id.my.santosa.notagampang.viewmodel.ShiftManagementViewModel
import id.my.santosa.notagampang.viewmodel.ShiftManagementViewModelFactory
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModel
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

sealed class Screen {
  object FloatingTabs : Screen()

  object Management : Screen()

  object ShiftManagement : Screen()

  object Kasbon : Screen()

  data class OrderEntry(
    val groupId: Long,
    val isReadOnly: Boolean = false,
    val fromKasbon: Boolean = false,
  ) : Screen()

  data class Checkout(
    val groupId: Long,
    val isReadOnly: Boolean = false,
    val fromKasbon: Boolean = false,
  ) : Screen()

  object Settings : Screen()
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val container = (application as NotaGampangApplication).container
    val preferenceManager = container.preferenceManager

    seedDefaultMenu(container.menuItemRepository)
    seedDefaultCategories(container.categoryRepository)
    seedDefaultPresets(container.suggestionPresetRepository)

    setContent {
      val settingsViewModel: SettingsViewModel =
        viewModel(factory = SettingsViewModelFactory(preferenceManager))
      val themeMode by settingsViewModel.themeMode.collectAsState()
      val darkTheme =
        when (themeMode) {
          ThemeMode.LIGHT -> false
          ThemeMode.DARK -> true
          ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }

      NotaGampangTheme(darkTheme = darkTheme) {
        val floatingTabsViewModel: FloatingTabsViewModel =
          viewModel(
            factory =
              FloatingTabsViewModelFactory(
                container.customerGroupRepository,
              ),
          )
        val menuManagementViewModel: MenuManagementViewModel =
          viewModel(
            factory =
              MenuManagementViewModelFactory(
                container.menuItemRepository,
              ),
          )
        val presetsViewModel: SuggestionPresetsViewModel =
          viewModel(
            factory =
              SuggestionPresetsViewModelFactory(
                container.suggestionPresetRepository,
              ),
          )
        val categoryManagementViewModel: CategoryManagementViewModel =
          viewModel(
            factory =
              CategoryManagementViewModelFactory(
                container.categoryRepository,
              ),
          )
        val kasbonViewModel: KasbonViewModel =
          viewModel(
            factory =
              KasbonViewModelFactory(
                container.debtRecordRepository,
                container.customerGroupRepository,
                preferenceManager,
              ),
          )
        val shiftManagementViewModel: ShiftManagementViewModel =
          viewModel(
            factory =
              ShiftManagementViewModelFactory(
                container.orderRepository,
                container.customerGroupRepository,
                container.debtRecordRepository,
              ),
          )

        var currentScreen by remember { mutableStateOf<Screen>(Screen.FloatingTabs) }
        var showAddManagementSheet by remember { mutableStateOf(false) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var showMergeDialog by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current

        val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
        val backHandlerEnabled =
          (
            currentScreen !is Screen.FloatingTabs ||
              showAddManagementSheet ||
              showDeleteConfirm ||
              showMergeDialog
          ) && !isKeyboardVisible

        // Back navigation handler
        PriorityBackHandler(enabled = backHandlerEnabled) {
          focusManager.clearFocus()
          if (showAddManagementSheet) {
            showAddManagementSheet = false
          } else if (showDeleteConfirm) {
            showDeleteConfirm = false
          } else if (showMergeDialog) {
            showMergeDialog = false
          } else {
            currentScreen =
              when (val s = currentScreen) {
                is Screen.Checkout ->
                  Screen.OrderEntry(
                    s.groupId,
                    s.isReadOnly,
                    s.fromKasbon,
                  )
                is Screen.OrderEntry ->
                  if (s.fromKasbon) {
                    Screen.Kasbon
                  } else {
                    Screen.FloatingTabs
                  }
                is Screen.Settings -> Screen.FloatingTabs
                is Screen.Kasbon -> Screen.FloatingTabs
                is Screen.Management -> Screen.FloatingTabs
                is Screen.ShiftManagement -> Screen.FloatingTabs
                else -> Screen.FloatingTabs
              }
          }
        }

        val presets by
          presetsViewModel.presets.collectAsState(
            initial = emptyList(),
          )

        Scaffold(
          topBar = {
            val title =
              when (currentScreen) {
                is Screen.FloatingTabs -> "Nota Aktif"
                is Screen.Kasbon -> "Kasbon"
                is Screen.Management -> "Kelola"
                is Screen.ShiftManagement -> "Tutup Kasir (Shift)"
                is Screen.OrderEntry -> "Pesanan"
                is Screen.Checkout -> "Bayar"
                is Screen.Settings -> "Setelan"
              }

            val showTopBar = currentScreen !is Screen.FloatingTabs
            val showBack =
              currentScreen !is Screen.FloatingTabs &&
                currentScreen !is Screen.Kasbon &&
                currentScreen !is Screen.Management &&
                currentScreen !is Screen.ShiftManagement &&
                currentScreen !is Screen.Settings

            if (showTopBar) {
              @OptIn(ExperimentalMaterial3Api::class)
              CenterAlignedTopAppBar(
                title = {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =
                      Arrangement.spacedBy(
                        8.dp,
                      ),
                  ) {
                    if (!showBack) {
                      val topIcon =
                        when (currentScreen) {
                          is Screen.FloatingTabs ->
                            Icons.AutoMirrored.Filled.ReceiptLong
                          is Screen.Kasbon -> Icons.Default.Payments
                          is Screen.Management ->
                            Icons.Default.SettingsInputComponent
                          is Screen.ShiftManagement -> Icons.Default.ManageHistory
                          is Screen.Settings -> Icons.Default.Settings
                          else -> Icons.AutoMirrored.Filled.Notes
                        }
                      Icon(
                        topIcon,
                        contentDescription = null,
                        modifier =
                          Modifier.size(
                            24.dp,
                          ),
                        tint = MaterialTheme.colorScheme.primary,
                      )
                    }
                    Text(title)
                  }
                },
                navigationIcon = {
                  if (showBack) {
                    IconButton(
                      onClick = {
                        currentScreen =
                          when (val s = currentScreen) {
                            is Screen.OrderEntry ->
                              if (s.fromKasbon) {
                                Screen.Kasbon
                              } else {
                                Screen.FloatingTabs
                              }
                            is Screen.Checkout ->
                              Screen.OrderEntry(
                                s.groupId,
                                s.isReadOnly,
                                s.fromKasbon,
                              )
                            else -> Screen.FloatingTabs
                          }
                      },
                    ) {
                      Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                      )
                    }
                  }
                },
                actions = {
                  val screen = currentScreen
                  if (screen is Screen.OrderEntry) {
                    IconButton(
                      onClick = { showDeleteConfirm = true },
                    ) {
                      Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus Nota",
                        tint = MaterialTheme.colorScheme.error,
                      )
                    }
                  }
                },
                colors =
                  TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor =
                      MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor =
                      MaterialTheme.colorScheme.onSurface,
                  ),
              )
            }
          },
          bottomBar = {
            val showBottomBar =
              currentScreen is Screen.FloatingTabs ||
                currentScreen is Screen.Kasbon ||
                currentScreen is Screen.Management ||
                currentScreen is Screen.Settings

            if (showBottomBar) {
              NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
              ) {
                NavigationBarItem(
                  icon = {
                    Icon(
                      Icons.AutoMirrored.Filled.ReceiptLong,
                      "Nota",
                    )
                  },
                  label = { Text("Nota") },
                  selected = currentScreen is Screen.FloatingTabs,
                  onClick = { currentScreen = Screen.FloatingTabs },
                  colors =
                    NavigationBarItemDefaults.colors(
                      selectedIconColor =
                        MaterialTheme.colorScheme.onSecondary,
                      selectedTextColor =
                        MaterialTheme.colorScheme.secondary,
                      unselectedIconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                      unselectedTextColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                      indicatorColor = MaterialTheme.colorScheme.secondary,
                    ),
                )
                NavigationBarItem(
                  icon = {
                    Icon(
                      Icons.Default.Payments,
                      "Kasbon",
                    )
                  },
                  label = { Text("Kasbon") },
                  selected = currentScreen is Screen.Kasbon,
                  onClick = { currentScreen = Screen.Kasbon },
                  colors =
                    NavigationBarItemDefaults.colors(
                      selectedIconColor =
                        MaterialTheme.colorScheme.onSecondary,
                      selectedTextColor =
                        MaterialTheme.colorScheme.secondary,
                      unselectedIconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                      unselectedTextColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                      indicatorColor = MaterialTheme.colorScheme.secondary,
                    ),
                )
                NavigationBarItem(
                  icon = {
                    Icon(
                      Icons.Default.SettingsInputComponent,
                      "Kelola",
                    )
                  },
                  label = { Text("Kelola") },
                  selected = currentScreen is Screen.Management,
                  onClick = { currentScreen = Screen.Management },
                  colors =
                    NavigationBarItemDefaults.colors(
                      selectedIconColor =
                        MaterialTheme.colorScheme.onSecondary,
                      selectedTextColor =
                        MaterialTheme.colorScheme.secondary,
                      unselectedIconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                      unselectedTextColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                      indicatorColor = MaterialTheme.colorScheme.secondary,
                    ),
                )
                NavigationBarItem(
                  icon = {
                    Icon(
                      Icons.Default.Settings,
                      "Setelan",
                    )
                  },
                  label = { Text("Setelan") },
                  selected = currentScreen is Screen.Settings,
                  onClick = { currentScreen = Screen.Settings },
                  colors =
                    NavigationBarItemDefaults.colors(
                      selectedIconColor =
                        MaterialTheme.colorScheme.onSecondary,
                      selectedTextColor =
                        MaterialTheme.colorScheme.secondary,
                      unselectedIconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                      unselectedTextColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                      indicatorColor = MaterialTheme.colorScheme.secondary,
                    ),
                )
              }
            }
          },
          floatingActionButton = {
            if (currentScreen is Screen.Management) {
              FloatingActionButton(
                onClick = { showAddManagementSheet = true },
                modifier =
                  Modifier.offset(
                    y = (-30).dp,
                  ),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
              ) { Icon(Icons.Default.Add, "Tambah") }
            }
          },
        ) { innerPadding ->
          val otherGroups by
            if (currentScreen is Screen.OrderEntry) {
              container
                .customerGroupRepository
                .getOtherActiveGroups(
                  (currentScreen as Screen.OrderEntry).groupId,
                )
                .collectAsState(
                  initial = emptyList(),
                )
            } else {
              remember {
                mutableStateOf(
                  emptyList<
                    CustomerGroupEntity,
                    >(),
                )
              }
            }

          if (showDeleteConfirm) {
            AlertDialog(
              onDismissRequest = { showDeleteConfirm = false },
              title = { Text("Hapus Nota") },
              text = {
                Text(
                  "Apakah Anda yakin ingin menghapus nota ini? Semua pesanan akan dihapus.",
                )
              },
              confirmButton = {
                Button(
                  onClick = {
                    val screen = currentScreen
                    if (screen is Screen.OrderEntry) {
                      scope.launch {
                        container.customerGroupRepository.deleteGroup(
                          screen.groupId,
                        )
                        currentScreen = Screen.FloatingTabs
                      }
                    }
                    showDeleteConfirm = false
                  },
                  colors =
                    ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) { Text("Hapus") }
              },
              dismissButton = {
                TextButton(
                  onClick = { showDeleteConfirm = false },
                ) { Text("Batal") }
              },
            )
          }

          if (showMergeDialog) {
            AlertDialog(
              onDismissRequest = { showMergeDialog = false },
              title = { Text("Gabung Nota") },
              text = {
                Column {
                  Text(
                    "Pilih nota tujuan untuk menggabungkan semua pesanan ini:",
                  )
                  if (otherGroups.isEmpty()) {
                    Text(
                      "Tidak ada nota aktif lain untuk digabungkan.",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.error,
                      modifier =
                        Modifier.padding(
                          top = 8.dp,
                        ),
                    )
                  } else {
                    otherGroups.forEach { other,
                      ->
                      Card(
                        modifier =
                          Modifier.fillMaxWidth()
                            .padding(
                              vertical = 4.dp,
                            )
                            .clickable {
                              scope.launch {
                                container.customerGroupRepository
                                  .mergeGroups(
                                    (
                                      currentScreen as
                                        Screen.OrderEntry
                                    )
                                      .groupId,
                                    other.id,
                                  )
                                currentScreen = Screen.FloatingTabs
                              }
                              showMergeDialog = false
                            },
                        colors =
                          CardDefaults.cardColors(
                            containerColor =
                              MaterialTheme.colorScheme
                                .surfaceVariant,
                          ),
                      ) {
                        Text(
                          other.alias,
                          modifier =
                            Modifier.padding(
                              16.dp,
                            ),
                          fontWeight = FontWeight.SemiBold,
                        )
                      }
                    }
                  }
                }
              },
              confirmButton = {},
              dismissButton = {
                TextButton(
                  onClick = { showMergeDialog = false },
                ) { Text("Batal") }
              },
            )
          }

          Surface(
            modifier =
              Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom =
                  if (currentScreen is Screen.FloatingTabs ||
                    currentScreen is Screen.Management
                  ) {
                    0.dp
                  } else {
                    innerPadding.calculateBottomPadding()
                  },
              ),
            color = MaterialTheme.colorScheme.background,
          ) {
            when (val screen = currentScreen) {
              is Screen.FloatingTabs -> {
                FloatingTabsScreen(
                  viewModel = floatingTabsViewModel,
                  suggestions = presets.map { it.label },
                  bottomPadding = innerPadding.calculateBottomPadding(),
                  onTabClick = { groupId, isReadOnly,
                    ->
                    currentScreen =
                      Screen.OrderEntry(
                        groupId,
                        isReadOnly,
                      )
                  },
                )
              }
              is Screen.Management -> {
                ManagementScreen(
                  menuViewModel = menuManagementViewModel,
                  presetsViewModel = presetsViewModel,
                  categoryViewModel = categoryManagementViewModel,
                  showAddSheet = showAddManagementSheet,
                  bottomPadding = innerPadding.calculateBottomPadding(),
                  onSheetDismiss = { showAddManagementSheet = false },
                )
              }
              is Screen.OrderEntry -> {
                val orderEntryViewModel: OrderEntryViewModel =
                  viewModel(
                    key = "OrderEntry_${screen.groupId}",
                    factory =
                      OrderEntryViewModelFactory(
                        screen.groupId,
                        container.customerGroupRepository,
                        container.menuItemRepository,
                        container.orderRepository,
                        container.categoryRepository,
                      ),
                  )
                OrderEntryScreen(
                  viewModel = orderEntryViewModel,
                  isReadOnly = screen.isReadOnly,
                  onCheckout = {
                    currentScreen =
                      Screen.Checkout(
                        screen.groupId,
                        screen.isReadOnly,
                        screen.fromKasbon,
                      )
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
                        container.orderRepository,
                        container.customerGroupRepository,
                        container.debtRecordRepository,
                      ),
                  )
                CheckoutScreen(
                  viewModel = checkoutViewModel,
                  isReadOnly = screen.isReadOnly,
                  onCheckoutComplete = { currentScreen = Screen.FloatingTabs },
                )
              }
              is Screen.Kasbon -> {
                KasbonScreen(
                  viewModel = kasbonViewModel,
                  onBack = { currentScreen = Screen.FloatingTabs },
                  onViewNote = { groupId ->
                    currentScreen =
                      Screen.OrderEntry(
                        groupId,
                        isReadOnly = true,
                        fromKasbon = true,
                      )
                  },
                )
              }
              is Screen.ShiftManagement -> {
                ShiftManagementScreen(
                  viewModel = shiftManagementViewModel,
                  onBack = { currentScreen = Screen.FloatingTabs },
                  onShiftClosed = { currentScreen = Screen.FloatingTabs },
                )
              }
              is Screen.Settings -> {
                SettingsScreen(
                  viewModel = settingsViewModel,
                )
              }
            }
          }
        }
      }
    }
  }

  private fun seedDefaultMenu(repository: IMenuItemRepository) {
    val scope = MainScope()
    scope.launch(Dispatchers.IO) {
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

  private fun seedDefaultCategories(repository: ICategoryRepository) {
    val scope = MainScope()
    scope.launch(Dispatchers.IO) {
      if (repository.getCount() == 0) {
        val categories =
          listOf(
            CategoryEntity(name = "Makanan"),
            CategoryEntity(name = "Minuman"),
            CategoryEntity(name = "Sate"),
            CategoryEntity(name = "Snack"),
          )
        for (category in categories) {
          repository.insertCategory(category)
        }
      }
    }
  }

  private fun seedDefaultPresets(repository: ISuggestionPresetRepository) {
    val scope = MainScope()
    scope.launch(Dispatchers.IO) {
      if (repository.getCount() == 0) {
        val presets =
          listOf(
            SuggestionPresetEntity(label = "Ojol"),
            SuggestionPresetEntity(label = "Karyawan"),
            SuggestionPresetEntity(label = "Anak Sekolah"),
            SuggestionPresetEntity(label = "Juru Parkir"),
            SuggestionPresetEntity(label = "Pelanggan"),
            SuggestionPresetEntity(label = "Member"),
            SuggestionPresetEntity(label = "Pak Bos"),
            SuggestionPresetEntity(label = "Bu Bos"),
          )
        for (preset in presets) {
          repository.insertPreset(preset)
        }
      }
    }
  }
}
