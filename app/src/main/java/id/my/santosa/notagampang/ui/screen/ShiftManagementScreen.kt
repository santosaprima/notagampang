package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.viewmodel.ShiftManagementViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftManagementScreen(
  viewModel: ShiftManagementViewModel,
  onBack: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsState()
  var showConfirmDialog by remember { mutableStateOf(false) }

  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
  currencyFormat.maximumFractionDigits = 0

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Tutup Kasir (Shift)") },
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
    Column(
      modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
        "Rangkuman Pendapatan",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
      )

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
          CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
          ),
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text("Pembayaran Lunas: ${currencyFormat.format(uiState.totalPaidIncome)}")
          Text("Pembayaran Kasbon: ${currencyFormat.format(uiState.totalKasbonIncome)}")
          Text(
            "Total Uang Masuk: ${currencyFormat.format(uiState.totalIncome)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }

      Text(
        "Rangkuman Kasbon",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
      )

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
          CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
          ),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            "Total Kasbon Aktif: ${currencyFormat.format(uiState.totalActiveKasbon)}",
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = FontWeight.Bold,
          )
        }
      }

      Button(
        onClick = { showConfirmDialog = true },
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
          ),
      ) { Text("Tutup Kasir (Bersihkan Antrean)") }
    }

    if (showConfirmDialog) {
      AlertDialog(
        onDismissRequest = { showConfirmDialog = false },
        title = { Text("Konfirmasi Tutup Kasir") },
        text = {
          Text(
            "Menutup kasir akan mengosongkan semua nota dan pesanan yang sudah lunas dari layar utama. " +
              "Tagihan dan menu tidak akan dihapus. Yakin lanjut?",
          )
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.closeShift {
                showConfirmDialog = false
                onBack()
              }
            },
            colors =
              ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
              ),
          ) { Text("Ya, Tutup Kasir") }
        },
        dismissButton = {
          TextButton(onClick = { showConfirmDialog = false }) { Text("Batal") }
        },
      )
    }
  }
}
