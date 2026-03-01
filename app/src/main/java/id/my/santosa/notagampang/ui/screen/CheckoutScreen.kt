package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.viewmodel.CheckoutViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CheckoutScreen(
        viewModel: CheckoutViewModel,
        onBack: () -> Unit = {},
        onCheckoutComplete: () -> Unit
) {
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(uiState.checkoutComplete) {
                if (uiState.checkoutComplete) {
                        onCheckoutComplete()
                }
        }

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        currencyFormat.maximumFractionDigits = 0

        val allIds = uiState.unpaidItems.map { it.id }.toSet()
        val allSelected = allIds.isNotEmpty() && uiState.selectedItemIds.containsAll(allIds)

        val totalToPay =
                uiState.unpaidItems.filter { uiState.selectedItemIds.contains(it.id) }.sumOf {
                        it.priceAtOrder * it.quantity
                }

        var cashReceivedStr by remember { mutableStateOf("") }
        var customerName by remember { mutableStateOf("") }
        var customerPhone by remember { mutableStateOf("") }

        val cashReceived = cashReceivedStr.toIntOrNull() ?: 0
        val isKasbon = cashReceived < totalToPay

        Column(modifier = Modifier.fillMaxSize().imePadding()) {
                if (uiState.isLoading) {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                                item {
                                        // Premium Header consistent with OrderEntry
                                        Row(
                                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                                text =
                                                                        "Nota #${uiState.group?.id ?: ""}",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelLarge,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .secondary,
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
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant
                                                                )
                                                        }
                                                }
                                        }

                                        HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 20.dp),
                                                color =
                                                        MaterialTheme.colorScheme.outlineVariant
                                                                .copy(alpha = 0.3f)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .clickable {
                                                                        if (allSelected) {
                                                                                viewModel
                                                                                        .clearSelection()
                                                                        } else {
                                                                                viewModel
                                                                                        .selectAll()
                                                                        }
                                                                }
                                                                .padding(
                                                                        horizontal = 20.dp,
                                                                        vertical = 12.dp
                                                                ),
                                                verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                                Checkbox(
                                                        checked = allSelected,
                                                        onCheckedChange = {
                                                                if (it) viewModel.selectAll()
                                                                else viewModel.clearSelection()
                                                        },
                                                )
                                                Text(
                                                        "Pilih Semua (Bayar Semua)",
                                                        modifier = Modifier.padding(start = 8.dp),
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                )
                                        }
                                }

                                items(uiState.unpaidItems, key = { it.id }) { item ->
                                        val isSelected = uiState.selectedItemIds.contains(item.id)
                                        Card(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(
                                                                        horizontal = 20.dp,
                                                                        vertical = 6.dp
                                                                )
                                                                .clickable {
                                                                        viewModel
                                                                                .toggleItemSelection(
                                                                                        item.id
                                                                                )
                                                                },
                                                shape = MaterialTheme.shapes.medium,
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        if (isSelected)
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.05f
                                                                                        )
                                                                        else
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surfaceVariant
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.3f
                                                                                        )
                                                        ),
                                                border =
                                                        if (isSelected)
                                                                androidx.compose.foundation
                                                                        .BorderStroke(
                                                                                1.dp,
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.5f
                                                                                        )
                                                                        )
                                                        else null,
                                                elevation =
                                                        CardDefaults.cardElevation(
                                                                defaultElevation = 0.dp
                                                        )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(16.dp),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                ) {
                                                        Checkbox(
                                                                checked = isSelected,
                                                                onCheckedChange = {
                                                                        viewModel
                                                                                .toggleItemSelection(
                                                                                        item.id
                                                                                )
                                                                },
                                                        )
                                                        Column(
                                                                modifier =
                                                                        Modifier.weight(1f)
                                                                                .padding(
                                                                                        start = 8.dp
                                                                                )
                                                        ) {
                                                                Text(
                                                                        item.customName
                                                                                ?: "Menu Item",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodyLarge,
                                                                        fontWeight = FontWeight.Bold
                                                                )
                                                                Text(
                                                                        "${item.quantity}x @ ${currencyFormat.format(item.priceAtOrder)}",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant
                                                                )
                                                        }
                                                        Text(
                                                                currencyFormat.format(
                                                                        item.priceAtOrder *
                                                                                item.quantity
                                                                ),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                                }
                                        }
                                }

                                item {
                                        // Summary Section moved inside LazyColumn (non-sticky)
                                        Column(
                                                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                        ) {
                                                HorizontalDivider(
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .outlineVariant.copy(
                                                                        alpha = 0.3f
                                                                )
                                                )

                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement =
                                                                Arrangement.SpaceBetween,
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Column {
                                                                Text(
                                                                        "Bayar",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleSmall,
                                                                        fontWeight =
                                                                                FontWeight.Bold,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurfaceVariant
                                                                )
                                                                Text(
                                                                        currencyFormat.format(
                                                                                totalToPay
                                                                        ),
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .headlineSmall,
                                                                        fontWeight =
                                                                                FontWeight
                                                                                        .ExtraBold,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary,
                                                                )
                                                        }
                                                }

                                                OutlinedTextField(
                                                        value = cashReceivedStr,
                                                        onValueChange = {
                                                                if (it.all { char ->
                                                                                char.isDigit()
                                                                        }
                                                                )
                                                                        cashReceivedStr = it
                                                        },
                                                        label = { Text("Uang Diterima (Rp)") },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Number
                                                                ),
                                                        singleLine = true,
                                                        shape = MaterialTheme.shapes.medium
                                                )

                                                if (isKasbon && totalToPay > 0) {
                                                        Card(
                                                                colors =
                                                                        CardDefaults.cardColors(
                                                                                containerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .errorContainer
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.5f
                                                                                                )
                                                                        ),
                                                                shape = MaterialTheme.shapes.medium,
                                                                modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                                Column(
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        12.dp
                                                                                )
                                                                ) {
                                                                        Text(
                                                                                "Sisa Pembayaran: ${currencyFormat.format(totalToPay - cashReceived)}",
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .error,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold
                                                                        )
                                                                        Text(
                                                                                "Akan dicatat sebagai Kasbon.",
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .error,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .labelSmall
                                                                        )
                                                                }
                                                        }
                                                        OutlinedTextField(
                                                                value = customerName,
                                                                onValueChange = {
                                                                        customerName = it
                                                                },
                                                                label = {
                                                                        Text(
                                                                                "Nama Lengkap Peminjam"
                                                                        )
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                singleLine = true,
                                                                shape = MaterialTheme.shapes.medium,
                                                                keyboardOptions =
                                                                        KeyboardOptions(
                                                                                capitalization =
                                                                                        KeyboardCapitalization
                                                                                                .Words
                                                                        ),
                                                        )
                                                        OutlinedTextField(
                                                                value = customerPhone,
                                                                onValueChange = {
                                                                        customerPhone = it
                                                                },
                                                                label = {
                                                                        Text(
                                                                                "No. WhatsApp (Opsional)"
                                                                        )
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                shape = MaterialTheme.shapes.medium,
                                                                keyboardOptions =
                                                                        KeyboardOptions(
                                                                                keyboardType =
                                                                                        KeyboardType
                                                                                                .Phone
                                                                        ),
                                                                singleLine = true,
                                                        )
                                                }

                                                Button(
                                                        onClick = {
                                                                viewModel.processCheckout(
                                                                        cashReceived,
                                                                        customerName,
                                                                        customerPhone,
                                                                )
                                                        },
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(56.dp),
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
                                                        enabled =
                                                                uiState.selectedItemIds
                                                                        .isNotEmpty() &&
                                                                        (!isKasbon ||
                                                                                customerName
                                                                                        .isNotBlank()),
                                                ) {
                                                        Text(
                                                                if (isKasbon)
                                                                        "Selesaikan & Catat Kasbon"
                                                                else "Bayar Lunas",
                                                                fontWeight = FontWeight.ExtraBold
                                                        )
                                                }
                                                Spacer(modifier = Modifier.height(32.dp))
                                        }
                                }
                        }
                }
        }
}
