package id.my.santosa.notagampang

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.santosa.notagampang.database.AppDatabase
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import id.my.santosa.notagampang.repository.CustomerGroupRepository
import id.my.santosa.notagampang.repository.MenuItemRepository
import id.my.santosa.notagampang.repository.OrderRepository
import id.my.santosa.notagampang.ui.screen.*
import id.my.santosa.notagampang.ui.theme.NotaGampangTheme
import id.my.santosa.notagampang.viewmodel.*
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

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                val database = AppDatabase.getDatabase(this)
                val groupRepository =
                        CustomerGroupRepository(
                                database.customerGroupDao(),
                                database.orderItemDao()
                        )
                val presetDao = database.suggestionPresetDao()
                val menuRepository = MenuItemRepository(database.menuItemDao())
                val orderRepository = OrderRepository(database.orderItemDao())
                val debtRecordRepository =
                        id.my.santosa.notagampang.repository.DebtRecordRepository(
                                database.debtRecordDao()
                        )

                setContent {
                        NotaGampangTheme {
                                Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = MaterialTheme.colorScheme.background,
                                ) {
                                        var currentScreen by remember {
                                                mutableStateOf<Screen>(Screen.FloatingTabs)
                                        }
                                        var showDeleteConfirm by remember { mutableStateOf(false) }
                                        var showMergeDialog by remember { mutableStateOf(false) }
                                        var showAddMenuSheet by remember { mutableStateOf(false) }
                                        val scope = rememberCoroutineScope()

                                        // ViewModels
                                        val floatingTabsViewModel: FloatingTabsViewModel =
                                                viewModel(
                                                        factory =
                                                                FloatingTabsViewModelFactory(
                                                                        groupRepository
                                                                )
                                                )
                                        val presetsViewModel: SuggestionPresetsViewModel =
                                                viewModel(
                                                        factory =
                                                                SuggestionPresetsViewModelFactory(
                                                                        presetDao
                                                                )
                                                )
                                        val menuManagementViewModel: MenuManagementViewModel =
                                                viewModel(
                                                        factory =
                                                                MenuManagementViewModelFactory(
                                                                        menuRepository
                                                                )
                                                )
                                        val kasbonViewModel: KasbonViewModel =
                                                viewModel(
                                                        factory =
                                                                KasbonViewModelFactory(
                                                                        debtRecordRepository
                                                                )
                                                )
                                        val shiftManagementViewModel: ShiftManagementViewModel =
                                                viewModel(
                                                        factory =
                                                                ShiftManagementViewModelFactory(
                                                                        orderRepository,
                                                                        groupRepository,
                                                                        debtRecordRepository
                                                                )
                                                )

                                        // First-run seeding
                                        val isFirstRunFlow =
                                                dataStore.data.map { preferences ->
                                                        preferences[
                                                                booleanPreferencesKey(
                                                                        "is_first_run"
                                                                )]
                                                                ?: true
                                                }
                                        val isFirstRun by
                                                isFirstRunFlow.collectAsState(initial = null)

                                        LaunchedEffect(isFirstRun) {
                                                if (isFirstRun == true) {
                                                        presetsViewModel.seedDefaults()
                                                        seedDefaultMenu(menuRepository)
                                                        dataStore.edit { preferences ->
                                                                preferences[
                                                                        booleanPreferencesKey(
                                                                                "is_first_run"
                                                                        )] = false
                                                        }
                                                }
                                        }

                                        val presets by
                                                presetsViewModel.presets.collectAsState(
                                                        initial = emptyList()
                                                )

                                        Scaffold(
                                                topBar = {
                                                        val title =
                                                                when (currentScreen) {
                                                                        is Screen.FloatingTabs ->
                                                                                "Nota Aktif"
                                                                        is Screen.Kasbon ->
                                                                                "Buku Kasbon"
                                                                        is Screen.MenuManagement ->
                                                                                "Kelola Menu"
                                                                        is Screen.ShiftManagement ->
                                                                                "Tutup Kasir (Shift)"
                                                                        is Screen.SuggestionPresets ->
                                                                                "Atur Pilihan Cepat"
                                                                        is Screen.OrderEntry ->
                                                                                "Pesanan"
                                                                        is Screen.Checkout ->
                                                                                "Checkout"
                                                                }

                                                        val showTopBar =
                                                                currentScreen !is
                                                                        Screen.FloatingTabs
                                                        val showBack =
                                                                currentScreen !is
                                                                        Screen.FloatingTabs &&
                                                                        currentScreen !is
                                                                                Screen.Kasbon &&
                                                                        currentScreen !is
                                                                                Screen.MenuManagement &&
                                                                        currentScreen !is
                                                                                Screen.ShiftManagement &&
                                                                        currentScreen !is
                                                                                Screen.SuggestionPresets

                                                        if (showTopBar) {
                                                                CenterAlignedTopAppBar(
                                                                        title = {
                                                                                Row(
                                                                                        verticalAlignment =
                                                                                                Alignment
                                                                                                        .CenterVertically,
                                                                                        horizontalArrangement =
                                                                                                Arrangement
                                                                                                        .spacedBy(
                                                                                                                8.dp
                                                                                                        )
                                                                                ) {
                                                                                        if (!showBack
                                                                                        ) {
                                                                                                Icon(
                                                                                                        Icons.Default
                                                                                                                .Notes,
                                                                                                        contentDescription =
                                                                                                                null,
                                                                                                        modifier =
                                                                                                                Modifier.size(
                                                                                                                        24.dp
                                                                                                                ),
                                                                                                        tint =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .primary
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
                                                                                                                when (val s =
                                                                                                                                currentScreen
                                                                                                                ) {
                                                                                                                        is Screen.OrderEntry ->
                                                                                                                                Screen.FloatingTabs
                                                                                                                        is Screen.Checkout ->
                                                                                                                                Screen.OrderEntry(
                                                                                                                                        s.groupId
                                                                                                                                )
                                                                                                                        else ->
                                                                                                                                Screen.FloatingTabs
                                                                                                                }
                                                                                                }
                                                                                        ) {
                                                                                                Icon(
                                                                                                        Icons.AutoMirrored
                                                                                                                .Filled
                                                                                                                .ArrowBack,
                                                                                                        contentDescription =
                                                                                                                "Kembali"
                                                                                                )
                                                                                        }
                                                                                }
                                                                        },
                                                                        actions = {
                                                                                if (currentScreen is
                                                                                                Screen.OrderEntry
                                                                                ) {
                                                                                        IconButton(
                                                                                                onClick = {
                                                                                                        showMergeDialog =
                                                                                                                true
                                                                                                }
                                                                                        ) {
                                                                                                Icon(
                                                                                                        Icons.Default
                                                                                                                .CallMerge,
                                                                                                        contentDescription =
                                                                                                                "Gabung Nota"
                                                                                                )
                                                                                        }
                                                                                        IconButton(
                                                                                                onClick = {
                                                                                                        showDeleteConfirm =
                                                                                                                true
                                                                                                }
                                                                                        ) {
                                                                                                Icon(
                                                                                                        Icons.Default
                                                                                                                .Delete,
                                                                                                        contentDescription =
                                                                                                                "Hapus Nota",
                                                                                                        tint =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .error
                                                                                                )
                                                                                        }
                                                                                }
                                                                        },
                                                                        colors =
                                                                                TopAppBarDefaults
                                                                                        .centerAlignedTopAppBarColors(
                                                                                                containerColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .surface,
                                                                                                titleContentColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurface,
                                                                                                navigationIconContentColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurface,
                                                                                                actionIconContentColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurface
                                                                                        )
                                                                )
                                                        }
                                                },
                                                bottomBar = {
                                                        val showBottomBar =
                                                                currentScreen is
                                                                        Screen.FloatingTabs ||
                                                                        currentScreen is
                                                                                Screen.Kasbon ||
                                                                        currentScreen is
                                                                                Screen.MenuManagement ||
                                                                        currentScreen is
                                                                                Screen.ShiftManagement ||
                                                                        currentScreen is
                                                                                Screen.SuggestionPresets

                                                        if (showBottomBar) {
                                                                NavigationBar(
                                                                        containerColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surface,
                                                                        tonalElevation = 0.dp
                                                                ) {
                                                                        NavigationBarItem(
                                                                                icon = {
                                                                                        Icon(
                                                                                                Icons.AutoMirrored
                                                                                                        .Filled
                                                                                                        .ReceiptLong,
                                                                                                contentDescription =
                                                                                                        "Nota"
                                                                                        )
                                                                                },
                                                                                label = {
                                                                                        Text("Nota")
                                                                                },
                                                                                selected =
                                                                                        currentScreen is
                                                                                                Screen.FloatingTabs,
                                                                                onClick = {
                                                                                        currentScreen =
                                                                                                Screen.FloatingTabs
                                                                                }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.secondary, selectedTextColor = MaterialTheme.colorScheme.secondary, indicatorColor = MaterialTheme.colorScheme.primary)
                                                                        )
                                                                        NavigationBarItem(
                                                                                icon = {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .History,
                                                                                                contentDescription =
                                                                                                        "Kasbon"
                                                                                        )
                                                                                },
                                                                                label = {
                                                                                        Text(
                                                                                                "Kasbon"
                                                                                        )
                                                                                },
                                                                                selected =
                                                                                        currentScreen is
                                                                                                Screen.Kasbon,
                                                                                onClick = {
                                                                                        currentScreen =
                                                                                                Screen.Kasbon
                                                                                }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.secondary, selectedTextColor = MaterialTheme.colorScheme.secondary, indicatorColor = MaterialTheme.colorScheme.primary)
                                                                        )
                                                                        NavigationBarItem(
                                                                                icon = {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .RestaurantMenu,
                                                                                                contentDescription =
                                                                                                        "Menu"
                                                                                        )
                                                                                },
                                                                                label = {
                                                                                        Text("Menu")
                                                                                },
                                                                                selected =
                                                                                        currentScreen is
                                                                                                Screen.MenuManagement,
                                                                                onClick = {
                                                                                        currentScreen =
                                                                                                Screen.MenuManagement
                                                                                }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.secondary, selectedTextColor = MaterialTheme.colorScheme.secondary, indicatorColor = MaterialTheme.colorScheme.primary)
                                                                        )
                                                                        NavigationBarItem(
                                                                                icon = {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .ManageHistory,
                                                                                                contentDescription =
                                                                                                        "Shift"
                                                                                        )
                                                                                },
                                                                                label = {
                                                                                        Text(
                                                                                                "Shift"
                                                                                        )
                                                                                },
                                                                                selected =
                                                                                        currentScreen is
                                                                                                Screen.ShiftManagement,
                                                                                onClick = {
                                                                                        currentScreen =
                                                                                                Screen.ShiftManagement
                                                                                }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.secondary, selectedTextColor = MaterialTheme.colorScheme.secondary, indicatorColor = MaterialTheme.colorScheme.primary)
                                                                        )
                                                                        NavigationBarItem(
                                                                                icon = {
                                                                                        Icon(
                                                                                                Icons.Default
                                                                                                        .SettingsSuggest,
                                                                                                contentDescription =
                                                                                                        "Saran"
                                                                                        )
                                                                                },
                                                                                label = {
                                                                                        Text(
                                                                                                "Saran"
                                                                                        )
                                                                                },
                                                                                selected =
                                                                                        currentScreen is
                                                                                                Screen.SuggestionPresets,
                                                                                onClick = {
                                                                                        currentScreen =
                                                                                                Screen.SuggestionPresets
                                                                                }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.secondary, selectedTextColor = MaterialTheme.colorScheme.secondary, indicatorColor = MaterialTheme.colorScheme.primary)
                                                                        )
                                                                }
                                                        }
                                                },
                                                floatingActionButton = {
                                                        if (currentScreen is Screen.MenuManagement
                                                        ) {
                                                                FloatingActionButton(
                                                                        onClick = {
                                                                                showAddMenuSheet =
                                                                                        true
                                                                        },
                                                                        modifier = Modifier.offset(y = 20.dp), containerColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary,
                                                                        contentColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .secondary
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default.Add,
                                                                                contentDescription =
                                                                                        "Tambah"
                                                                        )
                                                                }
                                                        }
                                                }
                                        ) { innerPadding ->
                                                // State for dialogs
                                                val otherGroups by
                                                        if (currentScreen is Screen.OrderEntry) {
                                                                groupRepository
                                                                        .getOtherActiveGroups(
                                                                                (currentScreen as
                                                                                                Screen.OrderEntry)
                                                                                        .groupId
                                                                        )
                                                                        .collectAsState(
                                                                                initial =
                                                                                        emptyList()
                                                                        )
                                                        } else {
                                                                remember {
                                                                        mutableStateOf(
                                                                                emptyList<
                                                                                        id.my.santosa.notagampang.database.entity.CustomerGroupEntity>()
                                                                        )
                                                                }
                                                        }

                                                // Dialogs
                                                if (showDeleteConfirm) {
                                                        AlertDialog(
                                                                onDismissRequest = {
                                                                        showDeleteConfirm = false
                                                                },
                                                                title = { Text("Hapus Nota") },
                                                                text = {
                                                                        Text(
                                                                                "Apakah Anda yakin ingin menghapus nota ini? Semua pesanan akan dihapus."
                                                                        )
                                                                },
                                                                confirmButton = {
                                                                        Button(
                                                                                onClick = {
                                                                                        val screen =
                                                                                                currentScreen
                                                                                        if (screen is
                                                                                                        Screen.OrderEntry
                                                                                        ) {
                                                                                                scope
                                                                                                        .launch {
                                                                                                                groupRepository
                                                                                                                        .deleteGroup(
                                                                                                                                screen.groupId
                                                                                                                        )
                                                                                                                currentScreen =
                                                                                                                        Screen.FloatingTabs
                                                                                                        }
                                                                                        }
                                                                                        showDeleteConfirm =
                                                                                                false
                                                                                },
                                                                                colors =
                                                                                        ButtonDefaults
                                                                                                .buttonColors(
                                                                                                        containerColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .error
                                                                                                )
                                                                        ) { Text("Hapus") }
                                                                },
                                                                dismissButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        showDeleteConfirm =
                                                                                                false
                                                                                }
                                                                        ) { Text("Batal") }
                                                                }
                                                        )
                                                }

                                                if (showMergeDialog) {
                                                        AlertDialog(
                                                                onDismissRequest = {
                                                                        showMergeDialog = false
                                                                },
                                                                title = { Text("Gabung Nota") },
                                                                text = {
                                                                        Column {
                                                                                Text(
                                                                                        "Pilih nota tujuan untuk menggabungkan semua pesanan ini:"
                                                                                )
                                                                                if (otherGroups
                                                                                                .isEmpty()
                                                                                ) {
                                                                                        Text(
                                                                                                "Tidak ada nota aktif lain untuk digabungkan.",
                                                                                                style =
                                                                                                        MaterialTheme
                                                                                                                .typography
                                                                                                                .bodySmall,
                                                                                                color =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .error,
                                                                                                modifier =
                                                                                                        Modifier.padding(
                                                                                                                top =
                                                                                                                        8.dp
                                                                                                        )
                                                                                        )
                                                                                } else {
                                                                                        otherGroups
                                                                                                .forEach {
                                                                                                        other
                                                                                                        ->
                                                                                                        Card(
                                                                                                                modifier =
                                                                                                                        Modifier.fillMaxWidth()
                                                                                                                                .padding(
                                                                                                                                        vertical =
                                                                                                                                                4.dp
                                                                                                                                )
                                                                                                                                .clickable {
                                                                                                                                        scope
                                                                                                                                                .launch {
                                                                                                                                                        groupRepository
                                                                                                                                                                .mergeGroups(
                                                                                                                                                                        (currentScreen as
                                                                                                                                                                                        Screen.OrderEntry)
                                                                                                                                                                                .groupId,
                                                                                                                                                                        other.id
                                                                                                                                                                )
                                                                                                                                                        currentScreen =
                                                                                                                                                                Screen.FloatingTabs
                                                                                                                                                }
                                                                                                                                        showMergeDialog =
                                                                                                                                                false
                                                                                                                                },
                                                                                                                colors =
                                                                                                                        CardDefaults
                                                                                                                                .cardColors(
                                                                                                                                        containerColor =
                                                                                                                                                MaterialTheme
                                                                                                                                                        .colorScheme
                                                                                                                                                        .surfaceVariant
                                                                                                                                )
                                                                                                        ) {
                                                                                                                Text(
                                                                                                                        other.alias,
                                                                                                                        modifier =
                                                                                                                                Modifier.padding(
                                                                                                                                        16.dp
                                                                                                                                ),
                                                                                                                        fontWeight =
                                                                                                                                FontWeight
                                                                                                                                        .SemiBold
                                                                                                                )
                                                                                                        }
                                                                                                }
                                                                                }
                                                                        }
                                                                },
                                                                confirmButton = {},
                                                                dismissButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        showMergeDialog =
                                                                                                false
                                                                                }
                                                                        ) { Text("Batal") }
                                                                }
                                                        )
                                                }

                                                Surface(
                                                        modifier = Modifier.padding(innerPadding),
                                                        color = MaterialTheme.colorScheme.background
                                                ) {
                                                        when (val screen = currentScreen) {
                                                                is Screen.FloatingTabs -> {
                                                                        FloatingTabsScreen(
                                                                                viewModel =
                                                                                        floatingTabsViewModel,
                                                                                suggestions =
                                                                                        presets
                                                                                                .map {
                                                                                                        it.label
                                                                                                },
                                                                                onTabClick = {
                                                                                        groupId ->
                                                                                        currentScreen =
                                                                                                Screen.OrderEntry(
                                                                                                        groupId
                                                                                                )
                                                                                }
                                                                        )
                                                                }
                                                                is Screen.SuggestionPresets -> {
                                                                        SuggestionPresetsScreen(
                                                                                viewModel =
                                                                                        presetsViewModel
                                                                        )
                                                                }
                                                                is Screen.MenuManagement -> {
                                                                        MenuManagementScreen(
                                                                                viewModel =
                                                                                        menuManagementViewModel,
                                                                                showBottomSheet =
                                                                                        showAddMenuSheet,
                                                                                onSheetDismiss = {
                                                                                        showAddMenuSheet =
                                                                                                false
                                                                                }
                                                                        )
                                                                }
                                                                is Screen.OrderEntry -> {
                                                                        val orderEntryViewModel:
                                                                                OrderEntryViewModel =
                                                                                viewModel(
                                                                                        key =
                                                                                                "OrderEntry_${screen.groupId}",
                                                                                        factory =
                                                                                                OrderEntryViewModelFactory(
                                                                                                        screen.groupId,
                                                                                                        groupRepository,
                                                                                                        menuRepository,
                                                                                                        orderRepository
                                                                                                )
                                                                                )
                                                                        OrderEntryScreen(
                                                                                viewModel =
                                                                                        orderEntryViewModel,
                                                                                onCheckout = {
                                                                                        currentScreen =
                                                                                                Screen.Checkout(
                                                                                                        screen.groupId
                                                                                                )
                                                                                }
                                                                        )
                                                                }
                                                                is Screen.Checkout -> {
                                                                        val checkoutViewModel:
                                                                                CheckoutViewModel =
                                                                                viewModel(
                                                                                        key =
                                                                                                "Checkout_${screen.groupId}",
                                                                                        factory =
                                                                                                CheckoutViewModelFactory(
                                                                                                        screen.groupId,
                                                                                                        orderRepository,
                                                                                                        groupRepository,
                                                                                                        debtRecordRepository
                                                                                                )
                                                                                )
                                                                        CheckoutScreen(
                                                                                viewModel =
                                                                                        checkoutViewModel,
                                                                                onBack = {
                                                                                        currentScreen =
                                                                                                Screen.OrderEntry(
                                                                                                        screen.groupId
                                                                                                )
                                                                                },
                                                                                onCheckoutComplete = {
                                                                                        currentScreen =
                                                                                                Screen.FloatingTabs
                                                                                }
                                                                        )
                                                                }
                                                                is Screen.Kasbon -> {
                                                                        KasbonScreen(
                                                                                viewModel =
                                                                                        kasbonViewModel,
                                                                                onBack = {
                                                                                        currentScreen =
                                                                                                Screen.FloatingTabs
                                                                                }
                                                                        )
                                                                }
                                                                is Screen.ShiftManagement -> {
                                                                        ShiftManagementScreen(
                                                                                viewModel =
                                                                                        shiftManagementViewModel,
                                                                                onBack = {
                                                                                        currentScreen =
                                                                                                Screen.FloatingTabs
                                                                                },
                                                                                onShiftClosed = {
                                                                                        currentScreen =
                                                                                                Screen.FloatingTabs
                                                                                }
                                                                        )
                                                                }
                                                        }
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
                                                        category = "Minuman"
                                                ),
                                                MenuItemEntity(
                                                        name = "Teh Hangat",
                                                        price = 3000,
                                                        category = "Minuman"
                                                ),
                                                MenuItemEntity(
                                                        name = "Es Jeruk",
                                                        price = 4000,
                                                        category = "Minuman"
                                                ),
                                                MenuItemEntity(
                                                        name = "Kopi Hitam",
                                                        price = 4000,
                                                        category = "Minuman"
                                                ),
                                                MenuItemEntity(
                                                        name = "Sate Usus",
                                                        price = 2000,
                                                        category = "Sate"
                                                ),
                                                MenuItemEntity(
                                                        name = "Sate Kulit",
                                                        price = 2000,
                                                        category = "Sate"
                                                ),
                                                MenuItemEntity(
                                                        name = "Sate Telur Puyuh",
                                                        price = 3000,
                                                        category = "Sate"
                                                ),
                                                MenuItemEntity(
                                                        name = "Nasi Kucing (Teri)",
                                                        price = 3000,
                                                        category = "Makanan"
                                                ),
                                                MenuItemEntity(
                                                        name = "Nasi Kucing (Sambal)",
                                                        price = 3000,
                                                        category = "Makanan"
                                                ),
                                                MenuItemEntity(
                                                        name = "Nasi Kucing (Bandeng)",
                                                        price = 3500,
                                                        category = "Makanan"
                                                ),
                                                MenuItemEntity(
                                                        name = "Singkong Goreng",
                                                        price = 2000,
                                                        category = "Snack"
                                                ),
                                                MenuItemEntity(
                                                        name = "Tempe Mendoan",
                                                        price = 1500,
                                                        category = "Snack"
                                                )
                                        )
                                for (item in menu) {
                                        repository.insertMenuItem(item)
                                }
                        }
                }
        }
}
