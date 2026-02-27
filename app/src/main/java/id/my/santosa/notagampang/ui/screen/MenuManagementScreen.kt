package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.database.entity.MenuItemEntity
import id.my.santosa.notagampang.viewmodel.MenuManagementViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen(
  viewModel: MenuManagementViewModel,
  onBack: () -> Unit,
) {
  val menuItems by viewModel.menuItems.collectAsState()

  var name by remember { mutableStateOf("") }
  var priceStr by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Minuman") }

  val categories = listOf("Minuman", "Makanan", "Sate", "Snack")
  var expanded by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Kelola Menu") },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Kembali",
            )
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor =
              MaterialTheme.colorScheme.onPrimaryContainer,
          ),
      )
    },
  ) { padding ->
    LazyColumn(
      modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        Card(
          colors =
            CardDefaults.cardColors(
              containerColor =
                MaterialTheme.colorScheme.surfaceVariant.copy(
                  alpha = 0.5f,
                ),
            ),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Text(
              "Tambah Menu Baru",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
            )

            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Nama Menu (Cth: Es Teh)") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = priceStr,
                onValueChange = {
                  if (it.all { char -> char.isDigit() }) priceStr = it
                },
                label = { Text("Harga (Rp)") },
                keyboardOptions =
                  KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
              )

              ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f),
              ) {
                OutlinedTextField(
                  value = category,
                  onValueChange = {},
                  readOnly = true,
                  label = { Text("Kategori") },
                  trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                      expanded = expanded,
                    )
                  },
                  modifier = Modifier.menuAnchor(),
                )
                ExposedDropdownMenu(
                  expanded = expanded,
                  onDismissRequest = { expanded = false },
                ) {
                  categories.forEach { selection ->
                    DropdownMenuItem(
                      text = { Text(selection) },
                      onClick = {
                        category = selection
                        expanded = false
                      },
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
              },
              modifier = Modifier.fillMaxWidth(),
              enabled = name.isNotBlank() && priceStr.isNotBlank(),
            ) {
              Icon(Icons.Filled.Add, contentDescription = null)
              Text("Simpan ke Menu", modifier = Modifier.padding(start = 8.dp))
            }
          }
        }
      }

      item {
        Text(
          "Daftar Menu Saat Ini",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
        )
      }

      items(menuItems, key = { it.id }) { item ->
        MenuManagementItemCard(
          menuItem = item,
          onDelete = { viewModel.deleteMenuItem(item) },
        )
      }
    }
  }
}

@Composable
fun MenuManagementItemCard(
  menuItem: MenuItemEntity,
  onDelete: () -> Unit,
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
  currencyFormat.maximumFractionDigits = 0

  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Row(
      modifier = Modifier.padding(16.dp).fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          menuItem.name,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Bold,
        )
        Text(
          "${menuItem.category} • ${currencyFormat.format(menuItem.price)}",
          style = MaterialTheme.typography.bodySmall,
        )
      }
      IconButton(onClick = onDelete) {
        Icon(
          Icons.Filled.Delete,
          contentDescription = "Hapus",
          tint = MaterialTheme.colorScheme.error,
        )
      }
    }
  }
}
