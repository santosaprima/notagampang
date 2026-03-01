package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.viewmodel.OrderEntryViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderEntryScreen(viewModel: OrderEntryViewModel, onCheckout: () -> Unit) {
        val uiState by viewModel.uiState.collectAsState()
        val currencyFormat = remember {
                val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
                format.setMaximumFractionDigits(0)
                format
        }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize().imePadding()) {
                                Row(
                                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Column {
                                                Text(
                                                        text = "Nota #${uiState.group?.id ?: ""}",
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        fontWeight = FontWeight.Bold
                                                )
                                                uiState.group?.let { group ->
                                                        val dateFormat = remember {
                                                                SimpleDateFormat(
                                                                        "dd MMM yyyy • HH:mm",
                                                                        Locale.forLanguageTag(
                                                                                "id-ID"
                                                                        )
                                                                )
                                                        }
                                                        Text(
                                                                text =
                                                                        dateFormat.format(
                                                                                Date(
                                                                                        group.createdAt
                                                                                )
                                                                        ),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                }
                                        }
                                }

                                HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                        color =
                                                MaterialTheme.colorScheme.outlineVariant.copy(
                                                        alpha = 0.3f
                                                )
                                )

                                val categories = listOf("Minuman", "Makanan", "Sate", "Snack")
                                val groupedItems = uiState.menuItems.groupBy { it.category }

                                LazyColumn(
                                        modifier = Modifier.weight(1f),
                                        contentPadding =
                                                PaddingValues(
                                                        start = 20.dp,
                                                        top = 0.dp,
                                                        end = 20.dp,
                                                        bottom = 120.dp
                                                ),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        categories.forEach { cat ->
                                                val itemsInCategory =
                                                        groupedItems[cat] ?: emptyList()
                                                if (itemsInCategory.isNotEmpty()) {
                                                        item {
                                                                Text(
                                                                        cat,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleMedium,
                                                                        fontWeight =
                                                                                FontWeight
                                                                                        .ExtraBold,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary,
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        top = 16.dp,
                                                                                        bottom =
                                                                                                8.dp
                                                                                )
                                                                )
                                                        }
                                                        items(itemsInCategory, key = { it.id }) {
                                                                menu ->
                                                                val order =
                                                                        uiState.currentOrders.find {
                                                                                it.menuItemId ==
                                                                                        menu.id
                                                                        }
                                                                val quantity = order?.quantity ?: 0

                                                                MenuItemRow(
                                                                        name = menu.name,
                                                                        price = menu.price,
                                                                        quantity = quantity,
                                                                        currencyFormat =
                                                                                currencyFormat,
                                                                        onIncrease = {
                                                                                viewModel
                                                                                        .addItemToOrder(
                                                                                                menu
                                                                                        )
                                                                        },
                                                                        onDecrease = {
                                                                                viewModel
                                                                                        .removeItemFromOrder(
                                                                                                menu.id
                                                                                        )
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }

                        // Custom Bottom Bar for Checkout
                        val total = uiState.currentOrders.sumOf { it.priceAtOrder * it.quantity }
                        val totalItems = uiState.currentOrders.sumOf { it.quantity }
                        Surface(
                                tonalElevation = 0.dp,
                                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        ) {
                                Column {
                                        HorizontalDivider(
                                                color =
                                                        MaterialTheme.colorScheme.outlineVariant
                                                                .copy(alpha = 0.3f)
                                        )
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(
                                                                        horizontal = 20.dp,
                                                                        vertical = 16.dp
                                                                ),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Column {
                                                        Text(
                                                                "Total",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                        Text(
                                                                currencyFormat.format(total),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .headlineSmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .secondary,
                                                                fontWeight = FontWeight.ExtraBold
                                                        )
                                                        Text(
                                                                "$totalItems items",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                }
                                                Button(
                                                        onClick = onCheckout,
                                                        enabled =
                                                                uiState.currentOrders.isNotEmpty(),
                                                        shape = MaterialTheme.shapes.medium,
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary,
                                                                        contentColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .secondary
                                                                ),
                                                        contentPadding =
                                                                PaddingValues(
                                                                        horizontal = 32.dp,
                                                                        vertical = 12.dp
                                                                )
                                                ) { Text("Bayar", fontWeight = FontWeight.Bold) }
                                        }
                                }
                        }
                }
        }
}

@Composable
fun MenuItemRow(
        name: String,
        price: Int,
        quantity: Int,
        currencyFormat: NumberFormat,
        onIncrease: () -> Unit,
        onDecrease: () -> Unit
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
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                        currencyFormat.format(price),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                if (quantity > 0) {
                                        Surface(
                                                modifier = Modifier.size(32.dp),
                                                color =
                                                        MaterialTheme.colorScheme.primary.copy(
                                                                alpha = 0.1f
                                                        ),
                                                shape = MaterialTheme.shapes.small,
                                                onClick = onDecrease
                                        ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                                imageVector =
                                                                        Icons.Outlined
                                                                                .RemoveCircleOutline,
                                                                contentDescription = "Kurangi",
                                                                modifier = Modifier.size(20.dp),
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                                }
                                        }

                                        Text(
                                                quantity.toString(),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                }

                                Surface(
                                        modifier = Modifier.size(32.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = MaterialTheme.shapes.small,
                                        onClick = onIncrease
                                ) {
                                        Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                        Icons.Default.Add,
                                                        contentDescription = "Tambah",
                                                        modifier = Modifier.size(20.dp),
                                                        tint = MaterialTheme.colorScheme.secondary
                                                )
                                        }
                                }
                        }
                }
        }
}
