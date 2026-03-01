package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.viewmodel.MenuManagementViewModel
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
        menuViewModel: MenuManagementViewModel,
        presetsViewModel: SuggestionPresetsViewModel,
        showAddMenuSheet: Boolean,
        onSheetDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Menu", "Pilihan Cepat")

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
                                    fontWeight =
                                            if (selectedTab == index) FontWeight.Bold
                                            else FontWeight.Normal
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
                        MenuManagementTab(
                                viewModel = menuViewModel,
                                showBottomSheet = showAddMenuSheet,
                                onSheetDismiss = onSheetDismiss
                        )
                1 -> SuggestionPresetsTab(viewModel = presetsViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementTab(
        viewModel: MenuManagementViewModel,
        showBottomSheet: Boolean,
        onSheetDismiss: () -> Unit
) {
    val menuItems by viewModel.menuItems.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Minuman") }

    val categories = listOf("Minuman", "Makanan", "Sate", "Snack")
    val groupedItems = menuItems.groupBy { it.category }
    var expanded by remember { mutableStateOf(false) }

    LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                    PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.forEach { cat ->
            val itemsInCategory = groupedItems[cat] ?: emptyList()
            if (itemsInCategory.isNotEmpty()) {
                item {
                    Text(
                            cat,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(itemsInCategory, key = { it.id }) { item ->
                    ManagementItemCard(
                            name = item.name,
                            detail = "${item.category} • ${formatCurrency(item.price)}",
                            icon = Icons.Default.Inventory,
                            onDelete = { viewModel.deleteMenuItem(item) }
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
                onDismissRequest = onSheetDismiss,
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
        ) {
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

                OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Menu") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        keyboardOptions =
                                KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                            value = priceStr,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) priceStr = it
                            },
                            label = { Text("Harga (Rp)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1.4f),
                            shape = MaterialTheme.shapes.medium
                    )

                    ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kategori") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier.menuAnchor(),
                                shape = MaterialTheme.shapes.medium
                        )
                        ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                        ) {
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
                }

                Button(
                        onClick = {
                            val price = priceStr.toIntOrNull() ?: 0
                            viewModel.addMenuItem(name, price, category)
                            name = ""
                            priceStr = ""
                            onSheetDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = name.isNotBlank() && priceStr.isNotBlank(),
                        shape = MaterialTheme.shapes.medium,
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Menu", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SuggestionPresetsTab(viewModel: SuggestionPresetsViewModel) {
    val presets by viewModel.presets.collectAsState()
    var newLabel by remember { mutableStateOf("") }

    Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                    value = newLabel,
                    onValueChange = { newLabel = it },
                    label = { Text("Tambah pilihan baru") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
            )
            FloatingActionButton(
                    onClick = {
                        if (newLabel.isNotBlank()) {
                            viewModel.addPreset(newLabel)
                            newLabel = ""
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.medium
            ) { Icon(Icons.Filled.Add, contentDescription = "Tambah") }
        }

        if (presets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                        text = "Belum ada pilihan cepat.\nTambahkan di atas.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(presets, key = { it.id }) { preset ->
                    ManagementItemCard(
                            name = preset.label,
                            detail = "Pilihan Cepat",
                            icon = Icons.Default.SettingsSuggest,
                            onDelete = { viewModel.deletePreset(preset) }
                    )
                }
            }
        }
    }
}

@Composable
fun ManagementItemCard(
        name: String,
        detail: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        onDelete: () -> Unit
) {
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
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                    modifier = Modifier.size(40.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

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
