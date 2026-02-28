package id.my.santosa.notagampang.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import id.my.santosa.notagampang.viewmodel.KasbonViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasbonScreen(
  viewModel: KasbonViewModel,
  onBack: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val currencyFormat =
    remember {
      val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
      format.maximumFractionDigits = 0
      format
    }
  val dateFormat =
    remember {
      SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
    }

  var selectedRecordForPayment by remember { mutableStateOf<DebtRecordEntity?>(null) }
  var paymentAmountStr by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Buku Kasbon") },
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
    if (uiState.isLoading) {
      // Loading screen placeholder
    } else if (uiState.activeDebts.isEmpty()) {
      Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
      ) {
        Text(
          "Alhamdulillah, tidak ada Kasbon aktif!",
          style = MaterialTheme.typography.bodyLarge,
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        items(uiState.activeDebts, key = { it.id }) { record ->
          Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  record.customerName,
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.titleMedium,
                )
                Text(
                  dateFormat.format(Date(record.timestamp)),
                  style = MaterialTheme.typography.bodySmall,
                )
              }
              if (!record.customerPhone.isNullOrBlank()) {
                Text(
                  record.customerPhone,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.primary,
                )
              }

              Spacer(modifier = Modifier.padding(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text("Total Tagihan:")
                Text(currencyFormat.format(record.totalAmount))
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text("Sudah Dibayar:")
                Text(currencyFormat.format(record.paidAmount))
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text("Sisa Kasbon:", fontWeight = FontWeight.Bold)
                Text(
                  currencyFormat.format(record.remainingDebt),
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.error,
                )
              }

              Spacer(modifier = Modifier.padding(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
              ) {
                if (!record.customerPhone.isNullOrBlank()) {
                  OutlinedButton(
                    onClick = {
                      val phone = record.customerPhone
                      val formattedPhone =
                        if (phone.startsWith("0")) {
                          "62" + phone.drop(1)
                        } else {
                          phone
                        }
                      val message =
                        "Halo ${record.customerName}, mengingatkan ada tagihan di Angkringan sebesar ${currencyFormat.format(
                          record.remainingDebt,
                        )}. Terima kasih!"
                      val intent = Intent(Intent.ACTION_VIEW)
                      intent.data =
                        Uri.parse(
                          "https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(message)}",
                        )
                      context.startActivity(intent)
                    },
                  ) { Text("Tagih") }
                  Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                  onClick = {
                    selectedRecordForPayment = record
                    paymentAmountStr = ""
                  },
                ) { Text("Terima Cicilan") }
              }
            }
          }
        }
      }
    }
  }

  if (selectedRecordForPayment != null) {
    AlertDialog(
      onDismissRequest = { selectedRecordForPayment = null },
      title = { Text("Terima Pembayaran") },
      text = {
        Column {
          Text(
            "Sisa tagihan: ${currencyFormat.format(selectedRecordForPayment!!.remainingDebt)}",
          )
          Spacer(modifier = Modifier.padding(8.dp))
          OutlinedTextField(
            value = paymentAmountStr,
            onValueChange = {
              if (it.all { char -> char.isDigit() }) paymentAmountStr = it
            },
            label = { Text("Jumlah Dibayar (Rp)") },
            keyboardOptions =
              KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val amount = paymentAmountStr.toIntOrNull() ?: 0
            if (amount > 0) {
              viewModel.receiveInstallment(selectedRecordForPayment!!, amount)
            }
            selectedRecordForPayment = null
          },
          enabled = paymentAmountStr.toIntOrNull() ?: 0 > 0,
        ) { Text("Simpan") }
      },
      dismissButton = {
        TextButton(onClick = { selectedRecordForPayment = null }) { Text("Batal") }
      },
    )
  }
}
