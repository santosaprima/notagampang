package id.my.santosa.notagampang.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.viewmodel.CategoryManagementViewModel
import id.my.santosa.notagampang.viewmodel.MenuManagementViewModel
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
        menuViewModel: MenuManagementViewModel,
        presetsViewModel: SuggestionPresetsViewModel,
        categoryViewModel: CategoryManagementViewModel,
        showAddSheet: Boolean,
        bottomPadding: androidx.compose.ui.unit.Dp = 0.dp,
        onSheetDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(1) } // Default to Menu
    val tabs = listOf("Kategori", "Menu", "Pilihan Cepat")
    val sheetState = rememberModalBottomSheetState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row
        PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTab),
                            width = 64.dp,
                            color = MaterialTheme.colorScheme.secondary
                    )
                }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                    title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight =
                                            if (selectedTab == index) FontWeight.ExtraBold
                                            else FontWeight.SemiBold
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.secondary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 ->
                        CategoryManagementTab(
                                viewModel = categoryViewModel,
                                bottomPadding = bottomPadding
                        )
                1 ->
                        MenuManagementTab(
                                viewModel = menuViewModel,
                                categoryViewModel = categoryViewModel,
                                bottomPadding = bottomPadding
                        )
                2 ->
                        SuggestionPresetsTab(
                                viewModel = presetsViewModel,
                                bottomPadding = bottomPadding
                        )
            }
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(
                onDismissRequest = onSheetDismiss,
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
        ) {
            when (selectedTab) {
                0 -> AddCategorySheet(viewModel = categoryViewModel, onDismiss = onSheetDismiss)
                1 ->
                        AddMenuSheet(
                                viewModel = menuViewModel,
                                categoryViewModel = categoryViewModel,
                                onDismiss = onSheetDismiss
                        )
                2 -> AddPresetSheet(viewModel = presetsViewModel, onDismiss = onSheetDismiss)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementTab(
        viewModel: MenuManagementViewModel,
        categoryViewModel: CategoryManagementViewModel,
        bottomPadding: androidx.compose.ui.unit.Dp = 0.dp
) {
    val menuItems by viewModel.menuItems.collectAsState()
    val categoriesEntities by categoryViewModel.categories.collectAsState()
    val categories = categoriesEntities.map { it.name }
    val groupedItems = menuItems.groupBy { it.category }

    Box(modifier = Modifier.fillMaxSize()) {
        if (menuItems.isEmpty()) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 140.dp),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        text = "Belum ada menu.\nKetuk tombol + untuk menambah.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding =
                            PaddingValues(
                                    start = 20.dp,
                                    end = 20.dp,
                                    top = 24.dp,
                                    bottom = bottomPadding + 20.dp
                            ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.forEach { cat ->
                    val itemsInCategory = groupedItems[cat] ?: emptyList()
                    if (itemsInCategory.isNotEmpty()) {
                        item {
                            Text(
                                    cat,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(itemsInCategory, key = { it.id }) { item ->
                            ManagementItemCard(
                                    name = item.name,
                                    detail = "${item.category} • ${formatCurrency(item.price)}",
                                    onDelete = { viewModel.deleteMenuItem(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionPresetsTab(
        viewModel: SuggestionPresetsViewModel,
        bottomPadding: androidx.compose.ui.unit.Dp = 0.dp
) {
    val presets by viewModel.presets.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        if (presets.isEmpty()) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 140.dp),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        text = "Belum ada pilihan cepat.\nKetuk tombol + untuk menambah.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 24.dp, bottom = bottomPadding + 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(presets, key = { it.id }) { preset ->
                    ManagementItemCard(
                            name = preset.label,
                            detail = "Pilihan Cepat",
                            onDelete = { viewModel.deletePreset(preset) }
                    )
                }
            }
        }
    }
}

@Composable
fun ManagementItemCard(name: String, detail: String, onDelete: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
                modifier =
                        Modifier.padding(start = 16.dp, top = 16.dp, end = 4.dp, bottom = 16.dp)
                                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                        detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}

private fun formatCurrency(amount: Int): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
    currencyFormat.maximumFractionDigits = 0
    return currencyFormat.format(amount)
}

@Composable
fun CategoryManagementTab(
        viewModel: CategoryManagementViewModel,
        bottomPadding: androidx.compose.ui.unit.Dp = 0.dp
) {
    val categories by viewModel.categories.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember {
        mutableStateOf<id.my.santosa.notagampang.database.entity.CategoryEntity?>(null)
    }

    if (showDeleteDialog && categoryToDelete != null) {
        AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    categoryToDelete = null
                },
                confirmButton = {
                    TextButton(
                            onClick = {
                                categoryToDelete?.let { viewModel.deleteCategory(it) }
                                showDeleteDialog = false
                                categoryToDelete = null
                            }
                    ) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(
                            onClick = {
                                showDeleteDialog = false
                                categoryToDelete = null
                            }
                    ) { Text("Batal") }
                },
                title = { Text("Hapus Kategori?") },
                text = {
                    Text(
                            "Menghapus kategori \"${categoryToDelete?.name}\" juga akan menghapus SEMUA menu di dalamnya. Tindakan ini tidak bisa dibatalkan."
                    )
                }
        )
    }

    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        if (categories.isEmpty()) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 140.dp),
                    contentAlignment = Alignment.Center
            ) {
                Text(
                        text = "Belum ada kategori.\nKetuk tombol + untuk menambah.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 24.dp, bottom = bottomPadding + 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories, key = { it.id }) { cat ->
                    ManagementItemCard(
                            name = cat.name,
                            detail = "Kategori",
                            onDelete = {
                                categoryToDelete = cat
                                showDeleteDialog = true
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun AddCategorySheet(viewModel: CategoryManagementViewModel, onDismiss: () -> Unit) {
    val focusManager = LocalFocusManager.current
    BackHandler {
        focusManager.clearFocus()
        onDismiss()
    }
    var name by remember { mutableStateOf("") }
    Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
                "Tambah Kategori Baru",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Kategori") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        Button(
                onClick = {
                    viewModel.addCategory(name)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = name.isNotBlank(),
                shape = MaterialTheme.shapes.medium,
                colors =
                        ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                        )
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan Kategori", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuSheet(
        viewModel: MenuManagementViewModel,
        categoryViewModel: CategoryManagementViewModel,
        onDismiss: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    BackHandler {
        focusManager.clearFocus()
        onDismiss()
    }
    val categoriesEntities by categoryViewModel.categories.collectAsState()
    val categories = categoriesEntities.map { it.name }

    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Makanan") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(categories) {
        if (category !in categories && categories.isNotEmpty()) {
            category = categories.first()
        }
    }

    Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
                "Tambah Menu Baru",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategori") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { selection ->
                    DropdownMenuItem(
                            text = { Text(selection) },
                            onClick = {
                                category = selection
                                expanded = false
                            }
                    )
                }
            }
        }

        OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Menu") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        OutlinedTextField(
                value = priceStr,
                onValueChange = { if (it.all { char -> char.isDigit() }) priceStr = it },
                label = { Text("Harga (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
        )

        Button(
                onClick = {
                    val price = priceStr.toIntOrNull() ?: 0
                    viewModel.addMenuItem(name, price, category)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = name.isNotBlank() && priceStr.isNotBlank(),
                shape = MaterialTheme.shapes.medium,
                colors =
                        ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                        )
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan Menu", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AddPresetSheet(viewModel: SuggestionPresetsViewModel, onDismiss: () -> Unit) {
    val focusManager = LocalFocusManager.current
    BackHandler {
        focusManager.clearFocus()
        onDismiss()
    }
    var label by remember { mutableStateOf("") }
    Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
                "Tambah Pilihan Cepat",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Pilihan") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        Button(
                onClick = {
                    viewModel.addPreset(label)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = label.isNotBlank(),
                shape = MaterialTheme.shapes.medium,
                colors =
                        ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                        )
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan Pilihan", fontWeight = FontWeight.Bold)
        }
    }
}
