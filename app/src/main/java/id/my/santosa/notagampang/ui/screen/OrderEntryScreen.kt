package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import id.my.santosa.notagampang.viewmodel.OrderEntryViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderEntryScreen(
  viewModel: OrderEntryViewModel,
  onBack: () -> Unit,
  onCheckout: () -> Unit,
  onDeleteGroup: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsState()
  var showCustomItemDialog by remember { mutableStateOf(false) }
  var showDeleteDialog by remember { mutableStateOf(false) }

  val categories = listOf("Semua", "Minuman", "Makanan", "Sate", "Snack")
  val totalItems = uiState.currentOrders.sumOf { it.quantity }
  val totalPrice = uiState.currentOrders.sumOf { it.priceAtOrder * it.quantity }

  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
  currencyFormat.maximumFractionDigits = 0

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Pesan Menu") },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Kembali",
            )
          }
        },
        actions = {
          IconButton(onClick = { showDeleteDialog = true }) {
            Icon(
              Icons.Filled.Delete,
              contentDescription = "Hapus Nota",
            )
          }
          BadgedBox(
            badge = {
              if (totalItems > 0) {
                Badge { Text(totalItems.toString()) }
              }
            },
          ) {
            IconButton(onClick = onCheckout) {
              Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "Keranjang",
              )
            }
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
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
      // Category Selector
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp),
      ) {
        items(categories) { category ->
          FilterChip(
            selected = uiState.selectedCategory == category,
            onClick = { viewModel.setCategory(category) },
            label = { Text(category) },
          )
        }
      }

      // Menu Grid
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f),
      ) {
        items(uiState.menuItems, key = { it.id }) { menuItem ->
          MenuItemCard(
            menuItem = menuItem,
            onClick = { viewModel.addItemToOrder(menuItem) },
          )
        }

        // Custom Item Button
        item { CustomItemCard(onClick = { showCustomItemDialog = true }) }
      }

      if (totalItems > 0) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shadowElevation = 8.dp,
          color = MaterialTheme.colorScheme.surface,
        ) {
          Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                "Total Sementara",
                style = MaterialTheme.typography.labelMedium,
              )
              Text(
                text = currencyFormat.format(totalPrice),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
              )
            }
            Button(onClick = onCheckout) { Text("Bayar (Checkout)") }
          }
        }
      }
    }

    if (showCustomItemDialog) {
      CustomItemDialog(
        onDismiss = { showCustomItemDialog = false },
        onConfirm = { name, price ->
          viewModel.addCustomItem(name, price)
          showCustomItemDialog = false
        },
      )
    }

    if (showDeleteDialog) {
      AlertDialog(
        onDismissRequest = { showDeleteDialog = false },
        title = { Text("Hapus Nota") },
        text = {
          Text(
            "Apakah Anda yakin ingin menghapus nota ini? Semua pesanan di dalamnya akan dihapus.",
          )
        },
        confirmButton = {
          Button(
            onClick = {
              showDeleteDialog = false
              onDeleteGroup()
            },
          ) { Text("Ya, Hapus") }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
        },
      )
    }
  }
}

@Composable
fun MenuItemCard(
  menuItem: MenuItemEntity,
  onClick: () -> Unit,
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
  currencyFormat.maximumFractionDigits = 0

  Card(
    modifier = Modifier.fillMaxWidth().height(130.dp).clickable(onClick = onClick),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
      ),
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier.padding(12.dp).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = menuItem.name,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          maxLines = 2,
          modifier = Modifier.fillMaxWidth(),
        )
        Text(
          text = currencyFormat.format(menuItem.price),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }

      // Add icon overlay
      Box(
        modifier =
          Modifier.align(Alignment.BottomEnd)
            .padding(8.dp)
            .size(24.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          Icons.Filled.Add,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.size(16.dp),
        )
      }
    }
  }
}

@Composable
fun CustomItemCard(onClick: () -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth().height(130.dp).clickable(onClick = onClick),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
      ),
  ) {
    Column(
      modifier = Modifier.padding(12.dp).fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Icon(
        Icons.Filled.Add,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSecondaryContainer,
      )
      Text(
        text = "Item Bebas",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
      )
    }
  }
}

@Composable
fun CustomItemDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, Int) -> Unit,
) {
  var name by remember { mutableStateOf("") }
  var priceStr by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Item Tambahan") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nama Item (Cth: Risol Mayo)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
          value = priceStr,
          onValueChange = { if (it.all { char -> char.isDigit() }) priceStr = it },
          label = { Text("Harga (Rp)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val price = priceStr.toIntOrNull() ?: 0
          onConfirm(name, price)
        },
        enabled = name.isNotBlank() && priceStr.isNotBlank(),
      ) { Text("Tambah") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } },
  )
}
