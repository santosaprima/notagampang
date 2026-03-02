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
        isReadOnly: Boolean = false,
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
                if (isReadOnly) {
                        (uiState.unpaidItems + uiState.paidItems).sumOf {
                                it.priceAtOrder * it.quantity
                        }
                } else {
                        uiState.unpaidItems
                                .filter { uiState.selectedItemIds.contains(it.id) }
                                .sumOf { it.priceAtOrder * it.quantity }
                }

        var cashReceivedStr by remember { mutableStateOf("") }
        var customerName by remember { mutableStateOf("") }
        var customerPhone by remember { mutableStateOf("") }

        val cashReceived = cashReceivedStr.toIntOrNull() ?: 0
        val isKasbon = cashReceived < totalToPay

        Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                } else {
                        LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 140.dp)
                        ) {
                                item {
                                        // Premium Header consistent with OrderEntry
                                        Row(
                                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                        Row(
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically,
                                                                horizontalArrangement =
                                                                        Arrangement.spacedBy(8.dp)
                                                        ) {
                                                                Text(
                                                                        text =
                                                                                "Nota #${uiState.group?.id ?: ""}",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .labelLarge,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .secondary,
                                                                        fontWeight = FontWeight.Bold
                                                                )
                                                                if (isReadOnly) {
                                                                        Surface(
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.1f
                                                                                                ),
                                                                                shape =
                                                                                        MaterialTheme
                                                                                                .shapes
                                                                                                .extraSmall,
                                                                        ) {
                                                                                Text(
                                                                                        "SUDAH DIBAYAR",
                                                                                        modifier =
                                                                                                Modifier.padding(
                                                                                                        horizontal =
                                                                                                                6.dp,
                                                                                                        vertical =
                                                                                                                2.dp
                                                                                                ),
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .labelSmall,
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primary,
                                                                                        fontWeight =
                                                                                                FontWeight
                                                                                                        .Bold
                                                                                )
                                                                        }
                                                                }
                                                        }
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

                                        if (!isReadOnly && uiState.unpaidItems.isNotEmpty()) {
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
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                ) {
                                                        Checkbox(
                                                                checked = allSelected,
                                                                onCheckedChange = {
                                                                        if (it)
                                                                                viewModel
                                                                                        .selectAll()
                                                                        else
                                                                                viewModel
                                                                                        .clearSelection()
                                                                },
                                                                colors =
                                                                        CheckboxDefaults.colors(
                                                                                checkedColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .secondary
                                                                        )
                                                        )
                                                        Text(
                                                                "Pilih Semua (Bayar Semua)",
                                                                modifier =
                                                                        Modifier.padding(
                                                                                start = 8.dp
                                                                        ),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyLarge,
                                                                fontWeight = FontWeight.Bold,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                                }
                                        }
                                }

                                val itemsToDisplay =
                                        if (isReadOnly) uiState.unpaidItems + uiState.paidItems
                                        else uiState.unpaidItems

                                items(itemsToDisplay, key = { it.id }) { item ->
                                        val isSelected =
                                                isReadOnly ||
                                                        uiState.selectedItemIds.contains(item.id)
                                        Card(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(
                                                                        horizontal = 20.dp,
                                                                        vertical = 6.dp
                                                                )
                                                                .then(
                                                                        if (!isReadOnly)
                                                                                Modifier.clickable {
                                                                                        viewModel
                                                                                                .toggleItemSelection(
                                                                                                        item.id
                                                                                                )
                                                                                }
                                                                        else Modifier
                                                                ),
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
                                                        if (!isReadOnly) {
                                                                Checkbox(
                                                                        checked = isSelected,
                                                                        onCheckedChange = {
                                                                                viewModel
                                                                                        .toggleItemSelection(
                                                                                                item.id
                                                                                        )
                                                                        },
                                                                        colors =
                                                                                CheckboxDefaults
                                                                                        .colors(
                                                                                                checkedColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .secondary
                                                                                        )
                                                                )
                                                        }
                                                        Column(
                                                                modifier =
                                                                        Modifier.weight(1f)
                                                                                .padding(
                                                                                        start =
                                                                                                if (isReadOnly
                                                                                                )
                                                                                                        0.dp
                                                                                                else
                                                                                                        8.dp
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

                                if (!isReadOnly) {
                                        item {
                                                // Summary Section (Scrollable inputs)
                                                Column(
                                                        modifier =
                                                                Modifier.padding(20.dp)
                                                                        .fillMaxWidth(),
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(16.dp),
                                                ) {
                                                        HorizontalDivider(
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .outlineVariant
                                                                                .copy(alpha = 0.3f)
                                                        )

                                                        OutlinedTextField(
                                                                value = cashReceivedStr,
                                                                onValueChange = {
                                                                        if (it.all { char ->
                                                                                        char.isDigit()
                                                                                }
                                                                        )
                                                                                cashReceivedStr = it
                                                                },
                                                                label = {
                                                                        Text("Uang Diterima (Rp)")
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                keyboardOptions =
                                                                        KeyboardOptions(
                                                                                keyboardType =
                                                                                        KeyboardType
                                                                                                .Number
                                                                        ),
                                                                singleLine = true,
                                                                shape = MaterialTheme.shapes.medium
                                                        )

                                                        if (isKasbon && totalToPay > 0) {
                                                                Card(
                                                                        colors =
                                                                                CardDefaults
                                                                                        .cardColors(
                                                                                                containerColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .errorContainer
                                                                                                                .copy(
                                                                                                                        alpha =
                                                                                                                                0.5f
                                                                                                                )
                                                                                        ),
                                                                        shape =
                                                                                MaterialTheme.shapes
                                                                                        .medium,
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
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
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        singleLine = true,
                                                                        shape =
                                                                                MaterialTheme.shapes
                                                                                        .medium,
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
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        shape =
                                                                                MaterialTheme.shapes
                                                                                        .medium,
                                                                        keyboardOptions =
                                                                                KeyboardOptions(
                                                                                        keyboardType =
                                                                                                KeyboardType
                                                                                                        .Phone
                                                                                ),
                                                                        singleLine = true,
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }

                        // Sticky Footer (Total & Button)
                        Surface(
                                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                tonalElevation = 3.dp
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
                                                val itemsCount =
                                                        if (isReadOnly)
                                                                (uiState.unpaidItems +
                                                                                uiState.paidItems)
                                                                        .count()
                                                        else
                                                                uiState.unpaidItems.count {
                                                                        uiState.selectedItemIds
                                                                                .contains(it.id)
                                                                }
                                                Column {
                                                        Text(
                                                                "Total",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleSmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant,
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                                currencyFormat.format(totalToPay),
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .headlineSmall,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .secondary,
                                                        )
                                                        Text(
                                                                "$itemsCount items",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                }

                                                Button(
                                                        onClick = {
                                                                if (isReadOnly) {
                                                                        onCheckoutComplete()
                                                                } else {
                                                                        viewModel.processCheckout(
                                                                                cashReceived,
                                                                                customerName,
                                                                                customerPhone,
                                                                        )
                                                                }
                                                        },
                                                        enabled =
                                                                isReadOnly ||
                                                                        uiState.selectedItemIds
                                                                                .isNotEmpty(),
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
                                                        modifier = Modifier.height(56.dp)
                                                ) {
                                                        Text(
                                                                if (isReadOnly) "Selesai"
                                                                else if (isKasbon) "Catat Kasbon"
                                                                else "Bayar Lunas",
                                                                fontWeight = FontWeight.ExtraBold
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}
