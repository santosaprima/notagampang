package id.my.santosa.notagampang.ui.screen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import id.my.santosa.notagampang.viewmodel.MenuManagementViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen(
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
                        PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                                modifier =
                                                        Modifier.padding(top = 12.dp, bottom = 4.dp)
                                        )
                                }
                                items(itemsInCategory, key = { it.id }) { item ->
                                        MenuManagementItemCard(
                                                menuItem = item,
                                                onDelete = { viewModel.deleteMenuItem(item) }
                                        )
                                }
                        }
                }
        }

        if (showBottomSheet) {
                ModalBottomSheet(onDismissRequest = onSheetDismiss, sheetState = sheetState) {
                        Column(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(16.dp)
                                                .padding(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                Text(
                                        "Tambah Menu Baru",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                )

                                OutlinedTextField(
                                        value = name,
                                        onValueChange = { name = it },
                                        label = { Text("Nama Menu (Cth: Es Teh)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions =
                                                KeyboardOptions(
                                                        capitalization =
                                                                KeyboardCapitalization.Words
                                                )
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                                value = priceStr,
                                                onValueChange = {
                                                        if (it.all { char -> char.isDigit() })
                                                                priceStr = it
                                                },
                                                label = { Text("Harga (Rp)") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
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
                                                                ExposedDropdownMenuDefaults
                                                                        .TrailingIcon(
                                                                                expanded = expanded
                                                                        )
                                                        },
                                                        modifier = Modifier.menuAnchor()
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
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = name.isNotBlank() && priceStr.isNotBlank()
                                ) {
                                        Icon(Icons.Filled.Add, contentDescription = null)
                                        Text(
                                                "Simpan ke Menu",
                                                modifier = Modifier.padding(start = 8.dp)
                                        )
                                }
                        }
                }
        }
}

@Composable
fun MenuManagementItemCard(menuItem: MenuItemEntity, onDelete: () -> Unit) {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        currencyFormat.maximumFractionDigits = 0

        Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
                Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        menuItem.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                )
                                Text(
                                        "${menuItem.category} • ${currencyFormat.format(menuItem.price)}",
                                        style = MaterialTheme.typography.bodySmall
                                )
                        }
                        IconButton(onClick = onDelete) {
                                Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.error
                                )
                        }
                }
        }
}
