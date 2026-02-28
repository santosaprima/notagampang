package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.viewmodel.OrderEntryViewModel

@Composable
fun OrderEntryScreen(
        viewModel: OrderEntryViewModel,
        onBack: () -> Unit = {},
        onCheckout: () -> Unit
) {
        val uiState by viewModel.uiState.collectAsState()

        Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize().imePadding()) {
                        Text(
                                "Daftar Menu",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                        )

                        LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding =
                                        PaddingValues(
                                                bottom = 80.dp
                                        ) // Provide space for bottom bar
                        ) {
                                items(uiState.menuItems) { menu ->
                                        val order =
                                                uiState.currentOrders.find {
                                                        it.menuItemId == menu.id
                                                }
                                        val quantity = order?.quantity ?: 0

                                        MenuItemRow(
                                                name = menu.name,
                                                price = menu.price,
                                                quantity = quantity,
                                                onIncrease = { viewModel.addItemToOrder(menu) },
                                                onDecrease = {
                                                        viewModel.removeItemFromOrder(menu.id)
                                                }
                                        )
                                }
                        }
                }

                // Custom Bottom Bar for Checkout
                val total = uiState.currentOrders.sumOf { it.priceAtOrder * it.quantity }
                Surface(
                        tonalElevation = 8.dp,
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Column {
                                        Text("Total", style = MaterialTheme.typography.labelMedium)
                                        Text(
                                                "Rp $total",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                        )
                                }
                                Button(
                                        onClick = onCheckout,
                                        enabled = uiState.currentOrders.isNotEmpty()
                                ) { Text("Checkout") }
                        }
                }
        }
}

@Composable
fun MenuItemRow(
        name: String,
        price: Int,
        quantity: Int,
        onIncrease: () -> Unit,
        onDecrease: () -> Unit
) {
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                )
                                Text("Rp $price", style = MaterialTheme.typography.bodySmall)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onDecrease, enabled = quantity > 0) {
                                        Icon(
                                                imageVector = Icons.Default.RemoveCircleOutline,
                                                contentDescription = "Kurangi",
                                                tint =
                                                        if (quantity > 0)
                                                                MaterialTheme.colorScheme.primary
                                                        else MaterialTheme.colorScheme.outline
                                        )
                                }
                                Text(
                                        quantity.toString(),
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        style = MaterialTheme.typography.titleMedium
                                )
                                IconButton(onClick = onIncrease) {
                                        Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Tambah",
                                                tint = MaterialTheme.colorScheme.primary
                                        )
                                }
                        }
                }
        }
}
