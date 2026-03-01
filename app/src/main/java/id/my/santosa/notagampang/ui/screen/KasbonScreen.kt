package id.my.santosa.notagampang.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import id.my.santosa.notagampang.viewmodel.KasbonViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun KasbonScreen(viewModel: KasbonViewModel, onBack: () -> Unit = {}) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val currencyFormat = remember {
    val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
    format.maximumFractionDigits = 0
    format
  }
  val dateFormat = remember {
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
  }

  var selectedRecordForPayment by remember { mutableStateOf<DebtRecordEntity?>(null) }
  var paymentAmountStr by remember { mutableStateOf("") }

  if (uiState.isLoading) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator()
    }
  } else if (uiState.activeDebts.isEmpty()) {
    Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) { Text("Alhamdulillah, tidak ada Kasbon aktif!", style = MaterialTheme.typography.bodyLarge) }
  } else {
    LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(uiState.activeDebts, key = { it.id }) { record ->
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
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                      modifier = Modifier.size(40.dp),
                      color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                      shape = MaterialTheme.shapes.small
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                          "!",
                          style = MaterialTheme.typography.titleLarge,
                          fontWeight = FontWeight.ExtraBold,
                          color = MaterialTheme.colorScheme.error
                  )
                }
              }

              Spacer(modifier = Modifier.width(16.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                        record.customerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                        dateFormat.format(Date(record.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                        currencyFormat.format(record.remainingDebt),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.error
                )
                Text(
                        "Sisa Tagihan",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            if (!record.customerPhone.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                      record.customerPhone,
                      style = MaterialTheme.typography.bodySmall,
                      fontWeight = FontWeight.Medium,
                      color = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.padding(start = 56.dp)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              if (!record.customerPhone.isNullOrBlank()) {
                OutlinedButton(
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        onClick = {
                          val phone = record.customerPhone
                          val formattedPhone =
                                  if (phone.startsWith("0")) {
                                    "62" + phone.drop(1)
                                  } else {
                                    phone
                                  }
                          val message =
                                  "Halo ${record.customerName}, mengingatkan ada tagihan di Angkringan sebesar ${currencyFormat.format(record.remainingDebt)}. Terima kasih!"
                          val intent = Intent(Intent.ACTION_VIEW)
                          intent.data =
                                  Uri.parse(
                                          "https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(message)}"
                                  )
                          context.startActivity(intent)
                        }
                ) { Text("Tagih WA") }
              }

              Button(
                      modifier = Modifier.weight(1f),
                      shape = MaterialTheme.shapes.medium,
                      colors =
                              ButtonDefaults.buttonColors(
                                      containerColor = MaterialTheme.colorScheme.primary,
                                      contentColor = MaterialTheme.colorScheme.secondary
                              ),
                      onClick = {
                        selectedRecordForPayment = record
                        paymentAmountStr = ""
                      }
              ) { Text("Terima Cicilan") }
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
                        "Sisa tagihan: ${currencyFormat.format(selectedRecordForPayment!!.remainingDebt)}"
                )
                Spacer(modifier = Modifier.padding(8.dp))
                OutlinedTextField(
                        value = paymentAmountStr,
                        onValueChange = {
                          if (it.all { char -> char.isDigit() }) paymentAmountStr = it
                        },
                        label = { Text("Jumlah Dibayar") },
                        prefix = { Text("Rp ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
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
                      enabled = (paymentAmountStr.toIntOrNull() ?: 0) > 0,
                      shape = MaterialTheme.shapes.medium,
                      colors =
                              ButtonDefaults.buttonColors(
                                      containerColor = MaterialTheme.colorScheme.primary,
                                      contentColor = MaterialTheme.colorScheme.secondary
                              )
              ) { Text("Simpan") }
            },
            dismissButton = {
              TextButton(onClick = { selectedRecordForPayment = null }) { Text("Batal") }
            }
    )
  }
}
