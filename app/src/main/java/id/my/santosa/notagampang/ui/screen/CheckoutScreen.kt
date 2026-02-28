package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import id.my.santosa.notagampang.viewmodel.CheckoutViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
  viewModel: CheckoutViewModel,
  onBack: () -> Unit,
  onCheckoutComplete: () -> Unit,
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

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Checkout: ${uiState.group?.alias ?: ""}") },
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
    Column(modifier = Modifier.fillMaxSize().padding(padding).imePadding()) {
      if (uiState.isLoading) {
        // Loading state normally here
      } else {
        LazyColumn(modifier = Modifier.weight(1f)) {
          item {
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .clickable {
                    if (allSelected) {
                      viewModel.clearSelection()
                    } else {
                      viewModel.selectAll()
                    }
                  }
                  .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Checkbox(
                checked = allSelected,
                onCheckedChange = {
                  if (it) viewModel.selectAll() else viewModel.clearSelection()
                },
              )
              Text(
                "Pilih Semua (Bayar Semua)",
                modifier = Modifier.padding(start = 16.dp),
                fontWeight = FontWeight.Bold,
              )
            }
            HorizontalDivider()
          }

          items(uiState.unpaidItems, key = { it.id }) { item ->
            val isSelected = uiState.selectedItemIds.contains(item.id)
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .clickable { viewModel.toggleItemSelection(item.id) }
                  .padding(horizontal = 16.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Checkbox(
                checked = isSelected,
                onCheckedChange = { viewModel.toggleItemSelection(item.id) },
              )
              Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                Text(item.customName ?: "Menu Item", fontWeight = FontWeight.W600)
                Text(
                  "${item.quantity}x @ ${currencyFormat.format(item.priceAtOrder)}",
                  style = MaterialTheme.typography.bodySmall,
                )
              }
              Text(
                currencyFormat.format(item.priceAtOrder * item.quantity),
                fontWeight = FontWeight.Bold,
              )
            }
          }
        }

        Surface(
          modifier = Modifier.fillMaxWidth(),
          shadowElevation = 8.dp,
          color = MaterialTheme.colorScheme.surface,
        ) {
          Column(
            modifier =
              Modifier.padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                "Total Tagihan Terpilih",
                style = MaterialTheme.typography.titleMedium,
              )
              Text(
                currencyFormat.format(totalToPay),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
              )
            }

            OutlinedTextField(
              value = cashReceivedStr,
              onValueChange = { if (it.all { char -> char.isDigit() }) cashReceivedStr = it },
              label = { Text("Uang Diterima (Rp)") },
              modifier = Modifier.fillMaxWidth(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
            )

            if (isKasbon && totalToPay > 0) {
              Text(
                "Sisa Pembayaran: ${currencyFormat.format(totalToPay - cashReceived)}. Akan dicatat sebagai Kasbon.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
              )
              OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("Nama Lengkap Peminjam") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
              )
              OutlinedTextField(
                value = customerPhone,
                onValueChange = { customerPhone = it },
                label = { Text("No. WhatsApp (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
              modifier = Modifier.fillMaxWidth(),
              enabled =
                uiState.selectedItemIds.isNotEmpty() &&
                  (!isKasbon || customerName.isNotBlank()),
            ) { Text(if (isKasbon) "Selesaikan & Catat Kasbon" else "Bayar Lunas") }
          }
        }
      }
    }
  }
}
