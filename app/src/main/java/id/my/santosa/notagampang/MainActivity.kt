package id.my.santosa.notagampang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMerge
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.santosa.notagampang.data.PreferenceManager
import id.my.santosa.notagampang.data.ThemeMode
import id.my.santosa.notagampang.database.AppDatabase
import id.my.santosa.notagampang.database.entity.*
import id.my.santosa.notagampang.repository.CategoryRepository
import id.my.santosa.notagampang.repository.CustomerGroupRepository
import id.my.santosa.notagampang.repository.DebtRecordRepository
import id.my.santosa.notagampang.repository.MenuItemRepository
import id.my.santosa.notagampang.repository.OrderRepository
import id.my.santosa.notagampang.ui.screen.*
import id.my.santosa.notagampang.ui.theme.NotaGampangTheme
import id.my.santosa.notagampang.viewmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

sealed class Screen {
        object FloatingTabs : Screen()
        object Management : Screen()
        object ShiftManagement : Screen()
        object Kasbon : Screen()
        data class OrderEntry(val groupId: Long) : Screen()
        data class Checkout(val groupId: Long) : Screen()
        object Settings : Screen()
}

class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                enableEdgeToEdge()

                val db = AppDatabase.getDatabase(applicationContext)
                val groupRepository =
                        CustomerGroupRepository(db.customerGroupDao(), db.orderItemDao())
                val menuRepository = MenuItemRepository(db.menuItemDao())
                val orderRepository = OrderRepository(db.orderItemDao())
                val debtRecordRepository = DebtRecordRepository(db.debtRecordDao())
                val categoryRepository = CategoryRepository(db.categoryDao())
                val preferenceManager = PreferenceManager(applicationContext)

                seedDefaultMenu(menuRepository)
                seedDefaultCategories(categoryRepository)

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
                                                                groupRepository
                                                        )
                                        )
                                val menuManagementViewModel: MenuManagementViewModel =
                                        viewModel(
                                                factory =
                                                        MenuManagementViewModelFactory(
                                                                menuRepository
                                                        )
                                        )
                                val presetsViewModel: SuggestionPresetsViewModel =
                                        viewModel(
                                                factory =
                                                        SuggestionPresetsViewModelFactory(
                                                                db.suggestionPresetDao()
                                                        )
                                        )
                                val categoryManagementViewModel: CategoryManagementViewModel =
                                        viewModel(
                                                factory =
                                                        CategoryManagementViewModelFactory(
                                                                categoryRepository
                                                        )
                                        )
                                val kasbonViewModel: KasbonViewModel =
                                        viewModel(
                                                factory =
                                                        KasbonViewModelFactory(debtRecordRepository)
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

                                var currentScreen by remember {
                                        mutableStateOf<Screen>(Screen.FloatingTabs)
                                }
                                var showAddManagementSheet by remember { mutableStateOf(false) }
                                var showDeleteConfirm by remember { mutableStateOf(false) }
                                var showMergeDialog by remember { mutableStateOf(false) }
                                val scope = rememberCoroutineScope()

                                // Back navigation handler
                                BackHandler(enabled = currentScreen !is Screen.FloatingTabs) {
                                        currentScreen =
                                                when (val s = currentScreen) {
                                                        is Screen.Checkout ->
                                                                Screen.OrderEntry(s.groupId)
                                                        is Screen.OrderEntry -> Screen.FloatingTabs
                                                        is Screen.Settings -> Screen.FloatingTabs
                                                        is Screen.Kasbon -> Screen.FloatingTabs
                                                        is Screen.Management -> Screen.FloatingTabs
                                                        is Screen.ShiftManagement ->
                                                                Screen.FloatingTabs
                                                        else -> Screen.FloatingTabs
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
                                                                is Screen.Kasbon -> "Buku Kasbon"
                                                                is Screen.Management ->
                                                                        "Kelola & Pengaturan"
                                                                is Screen.ShiftManagement ->
                                                                        "Tutup Kasir (Shift)"
                                                                is Screen.OrderEntry -> "Pesanan"
                                                                is Screen.Checkout -> "Bayar"
                                                                is Screen.Settings -> "Setelan"
                                                        }

                                                val showTopBar =
                                                        currentScreen !is Screen.FloatingTabs
                                                val showBack =
                                                        currentScreen !is Screen.FloatingTabs &&
                                                                currentScreen !is Screen.Kasbon &&
                                                                currentScreen !is
                                                                        Screen.Management &&
                                                                currentScreen !is
                                                                        Screen.ShiftManagement &&
                                                                currentScreen !is Screen.Settings

                                                if (showTopBar) {
                                                        @OptIn(ExperimentalMaterial3Api::class)
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
                                                                                if (!showBack) {
                                                                                        Icon(
                                                                                                Icons.AutoMirrored
                                                                                                        .Filled
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
                                                                                                Icons.AutoMirrored
                                                                                                        .Filled
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
                                                        currentScreen is Screen.FloatingTabs ||
                                                                currentScreen is Screen.Kasbon ||
                                                                currentScreen is
                                                                        Screen.Management ||
                                                                currentScreen is
                                                                        Screen.ShiftManagement ||
                                                                currentScreen is Screen.Settings

                                                if (showBottomBar) {
                                                        NavigationBar(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .surface,
                                                                tonalElevation = 0.dp
                                                        ) {
                                                                NavigationBarItem(
                                                                        icon = {
                                                                                Icon(
                                                                                        Icons.AutoMirrored
                                                                                                .Filled
                                                                                                .ReceiptLong,
                                                                                        "Nota"
                                                                                )
                                                                        },
                                                                        label = { Text("Nota") },
                                                                        selected =
                                                                                currentScreen is
                                                                                        Screen.FloatingTabs,
                                                                        onClick = {
                                                                                currentScreen =
                                                                                        Screen.FloatingTabs
                                                                        },
                                                                        colors =
                                                                                NavigationBarItemDefaults
                                                                                        .colors(
                                                                                                selectedIconColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary,
                                                                                                selectedTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary,
                                                                                                unselectedIconColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant,
                                                                                                unselectedTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant,
                                                                                                indicatorColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary
                                                                                        )
                                                                )
                                                                NavigationBarItem(
                                                                        icon = {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .History,
                                                                                        "Kasbon"
                                                                                )
                                                                        },
                                                                        label = { Text("Kasbon") },
                                                                        selected =
                                                                                currentScreen is
                                                                                        Screen.Kasbon,
                                                                        onClick = {
                                                                                currentScreen =
                                                                                        Screen.Kasbon
                                                                        },
                                                                        colors =
                                                                                NavigationBarItemDefaults
                                                                                        .colors(
                                                                                                selectedIconColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary,
                                                                                                selectedTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary,
                                                                                                unselectedIconColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant,
                                                                                                unselectedTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant,
                                                                                                indicatorColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary
                                                                                        )
                                                                )
                                                                NavigationBarItem(
                                                                        icon = {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .SettingsInputComponent,
                                                                                        "Kelola"
                                                                                )
                                                                        },
                                                                        label = { Text("Kelola") },
                                                                        selected =
                                                                                currentScreen is
                                                                                        Screen.Management,
                                                                        onClick = {
                                                                                currentScreen =
                                                                                        Screen.Management
                                                                        },
                                                                        colors =
                                                                                NavigationBarItemDefaults
                                                                                        .colors(
                                                                                                selectedIconColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary,
                                                                                                selectedTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary,
                                                                                                unselectedIconColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant,
                                                                                                unselectedTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant,
                                                                                                indicatorColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary
                                                                                        )
                                                                )
                                                                NavigationBarItem(
                                                                        icon = {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .ManageHistory,
                                                                                        "Shift"
                                                                                )
                                                                        },
                                                                        label = { Text("Shift") },
                                                                        selected =
                                                                                currentScreen is
                                                                                        Screen.ShiftManagement,
                                                                        onClick = {
                                                                                currentScreen =
                                                                                        Screen.ShiftManagement
                                                                        },
                                                                        colors =
                                                                                NavigationBarItemDefaults
                                                                                        .colors(
                                                                                                selectedIconColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary,
                                                                                                selectedTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary,
                                                                                                unselectedIconColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant,
                                                                                                unselectedTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant,
                                                                                                indicatorColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary
                                                                                        )
                                                                )
                                                                NavigationBarItem(
                                                                        icon = {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .Settings,
                                                                                        "Setelan"
                                                                                )
                                                                        },
                                                                        label = { Text("Setelan") },
                                                                        selected =
                                                                                currentScreen is
                                                                                        Screen.Settings,
                                                                        onClick = {
                                                                                currentScreen =
                                                                                        Screen.Settings
                                                                        },
                                                                        colors =
                                                                                NavigationBarItemDefaults
                                                                                        .colors(
                                                                                                selectedIconColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary,
                                                                                                selectedTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary,
                                                                                                unselectedIconColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant,
                                                                                                unselectedTextColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant,
                                                                                                indicatorColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary
                                                                                        )
                                                                )
                                                        }
                                                }
                                        },
                                        floatingActionButton = {
                                                if (currentScreen is Screen.Management) {
                                                        FloatingActionButton(
                                                                onClick = {
                                                                        showAddManagementSheet =
                                                                                true
                                                                },
                                                                modifier =
                                                                        Modifier.offset(
                                                                                y = (-30).dp
                                                                        ),
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                contentColor =
                                                                        MaterialTheme.colorScheme
                                                                                .secondary
                                                        ) { Icon(Icons.Default.Add, "Tambah") }
                                                }
                                        }
                                ) { innerPadding ->
                                        val otherGroups by
                                                if (currentScreen is Screen.OrderEntry) {
                                                        groupRepository
                                                                .getOtherActiveGroups(
                                                                        (currentScreen as
                                                                                        Screen.OrderEntry)
                                                                                .groupId
                                                                )
                                                                .collectAsState(
                                                                        initial = emptyList()
                                                                )
                                                } else {
                                                        remember {
                                                                mutableStateOf(
                                                                        emptyList<
                                                                                CustomerGroupEntity>()
                                                                )
                                                        }
                                                }

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
                                                                        if (otherGroups.isEmpty()) {
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
                                                modifier =
                                                        Modifier.padding(
                                                                top =
                                                                        innerPadding
                                                                                .calculateTopPadding(),
                                                                bottom =
                                                                        if (currentScreen is
                                                                                        Screen.FloatingTabs ||
                                                                                        currentScreen is
                                                                                                Screen.Management
                                                                        )
                                                                                0.dp
                                                                        else
                                                                                innerPadding
                                                                                        .calculateBottomPadding()
                                                        ),
                                                color = MaterialTheme.colorScheme.background
                                        ) {
                                                when (val screen = currentScreen) {
                                                        is Screen.FloatingTabs -> {
                                                                FloatingTabsScreen(
                                                                        viewModel =
                                                                                floatingTabsViewModel,
                                                                        suggestions =
                                                                                presets.map {
                                                                                        it.label
                                                                                },
                                                                        bottomPadding =
                                                                                innerPadding
                                                                                        .calculateBottomPadding(),
                                                                        onTabClick = { groupId ->
                                                                                currentScreen =
                                                                                        Screen.OrderEntry(
                                                                                                groupId
                                                                                        )
                                                                        }
                                                                )
                                                        }
                                                        is Screen.Management -> {
                                                                ManagementScreen(
                                                                        menuViewModel =
                                                                                menuManagementViewModel,
                                                                        presetsViewModel =
                                                                                presetsViewModel,
                                                                        categoryViewModel =
                                                                                categoryManagementViewModel,
                                                                        showAddSheet =
                                                                                showAddManagementSheet,
                                                                        bottomPadding =
                                                                                innerPadding
                                                                                        .calculateBottomPadding(),
                                                                        onSheetDismiss = {
                                                                                showAddManagementSheet =
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
                                                                                                orderRepository,
                                                                                                categoryRepository
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
                                                                        onCheckoutComplete = {
                                                                                currentScreen =
                                                                                        Screen.FloatingTabs
                                                                        }
                                                                )
                                                        }
                                                        is Screen.Kasbon -> {
                                                                KasbonScreen(
                                                                        viewModel = kasbonViewModel,
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
                                                        is Screen.Settings -> {
                                                                SettingsScreen(
                                                                        viewModel =
                                                                                settingsViewModel
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }

        private fun seedDefaultMenu(repository: MenuItemRepository) {
                val scope = MainScope()
                scope.launch(Dispatchers.IO) {
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

        private fun seedDefaultCategories(repository: CategoryRepository) {
                val scope = MainScope()
                scope.launch(Dispatchers.IO) {
                        if (repository.getCount() == 0) {
                                val categories =
                                        listOf(
                                                CategoryEntity(name = "Makanan"),
                                                CategoryEntity(name = "Minuman"),
                                                CategoryEntity(name = "Sate"),
                                                CategoryEntity(name = "Snack")
                                        )
                                for (category in categories) {
                                        repository.insertCategory(category)
                                }
                        }
                }
        }
}
